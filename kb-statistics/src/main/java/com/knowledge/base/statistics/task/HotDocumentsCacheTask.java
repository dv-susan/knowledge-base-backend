package com.knowledge.base.statistics.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.knowledge.base.statistics.entity.DocumentStatistics;
import com.knowledge.base.statistics.entity.UserStatistics;
import com.knowledge.base.statistics.mapper.DocumentStatisticsMapper;
import com.knowledge.base.statistics.mapper.UserStatisticsMapper;
import com.knowledge.base.statistics.vo.HotDocumentVO;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 热门文档缓存定时刷新任务
 *
 * <p>每30分钟根据文档的浏览量、点赞数、收藏数计算复合热度分数，
 * 取 Top 6 文档并写入 Redis + Caffeine 本地缓存。</p>
 *
 * <p>热度计算公式：score = viewCount * 1 + likeCount * 3 + favoriteCount * 5</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Component
public class HotDocumentsCacheTask {

    @Resource
    private DocumentStatisticsMapper documentMapper;

    @Resource
    private UserStatisticsMapper userMapper;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource(name = "caffeineCacheManager")
    private CacheManager caffeineCacheManager;

    private static final String REDIS_KEY = "stats:hotDocuments:top6";
    private static final String CAFFEINE_CACHE_NAME = "hotDocuments";
    private static final String CAFFEINE_CACHE_KEY = "top6";
    private static final int TOP_N = 6;

    /** 浏览量权重 */
    private static final int SCORE_VIEW = 1;
    /** 点赞权重 */
    private static final int SCORE_LIKE = 3;
    /** 收藏权重 */
    private static final int SCORE_FAVORITE = 5;

    /**
     * 服务启动后立即执行一次刷新
     */
    @PostConstruct
    public void init() {
        log.info("HotDocumentsCacheTask 初始化，开始首次热门文档缓存刷新...");
        refreshHotDocuments();
    }

    /**
     * 每30分钟刷新热门文档缓存
     */
    @Scheduled(fixedRate = 1800000)
    public void refreshHotDocuments() {
        log.info("开始刷新热门文档缓存...");
        try {
            List<HotDocumentVO> topDocs = computeTopDocuments();

            if (topDocs == null || topDocs.isEmpty()) {
                log.warn("未找到任何文档，跳过缓存刷新");
                return;
            }

            // 写入 Redis (L2)
            redisTemplate.opsForValue().set(REDIS_KEY, topDocs);
            log.debug("热门文档已写入 Redis: key={}, count={}", REDIS_KEY, topDocs.size());

            // 写入 Caffeine 本地缓存 (L1)
            org.springframework.cache.Cache cache = caffeineCacheManager.getCache(CAFFEINE_CACHE_NAME);
            if (cache != null) {
                cache.put(CAFFEINE_CACHE_KEY, topDocs);
                log.debug("热门文档已写入 Caffeine: cache={}, key={}", CAFFEINE_CACHE_NAME, CAFFEINE_CACHE_KEY);
            } else {
                log.warn("Caffeine 缓存 '{}' 不存在，请检查 CacheConfig 配置", CAFFEINE_CACHE_NAME);
            }

            log.info("热门文档缓存刷新完成：共 {} 条", topDocs.size());
        } catch (Exception e) {
            log.error("刷新热门文档缓存失败", e);
        }
    }

    /**
     * 查询并计算 Top N 热门文档
     *
     * <p>使用复合热度评分：score = viewCount * 1 + likeCount * 3 + favoriteCount * 5</p>
     */
    List<HotDocumentVO> computeTopDocuments() {
        // 1. 查询所有未删除的文档（按浏览量预筛选，减少计算量）
        List<DocumentStatistics> allDocs = documentMapper.selectList(
                new LambdaQueryWrapper<DocumentStatistics>()
                        .eq(DocumentStatistics::getStatus, 1)
                        .eq(DocumentStatistics::getDeleted, 0)
                        .gt(DocumentStatistics::getViewCount, 0)
                        .orderByDesc(DocumentStatistics::getViewCount)
                        .last("LIMIT 500"));

        if (allDocs == null || allDocs.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 按复合热度评分排序，取 Top N
        List<DocumentStatistics> topDocs = allDocs.stream()
                .sorted((a, b) -> Long.compare(compositeScore(b), compositeScore(a)))
                .limit(TOP_N)
                .collect(Collectors.toList());

        // 3. 批量获取作者名称映射
        Map<Long, String> authorNameMap = buildAuthorNameMap(topDocs);

        // 4. 批量获取分类名称映射
        Map<Long, String> categoryNameMap = buildCategoryNameMap(topDocs);

        // 5. 批量获取文档摘要映射
        Map<Long, String> summaryMap = buildSummaryMap(topDocs);

        // 6. 组装 VO 列表
        return topDocs.stream()
                .map(doc -> HotDocumentVO.builder()
                        .documentId(doc.getId())
                        .title(doc.getTitle())
                        .authorId(doc.getAuthorId())
                        .authorName(authorNameMap.getOrDefault(doc.getAuthorId(), ""))
                        .categoryId(doc.getCategoryId())
                        .categoryName(categoryNameMap.getOrDefault(doc.getCategoryId(), ""))
                        .viewCount(nullSafe(doc.getViewCount()))
                        .likeCount(nullSafe(doc.getLikeCount()))
                        .favoriteCount(nullSafe(doc.getFavoriteCount()))
                        .commentCount(0L)
                        .summary(summaryMap.getOrDefault(doc.getId(), ""))
                        .statisticsValue(compositeScore(doc))
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 计算文档的复合热度评分
     */
    private long compositeScore(DocumentStatistics doc) {
        return nullSafe(doc.getViewCount()) * SCORE_VIEW
                + nullSafe(doc.getLikeCount()) * SCORE_LIKE
                + nullSafe(doc.getFavoriteCount()) * SCORE_FAVORITE;
    }

    /**
     * 批量构建作者 ID → 用户名映射
     */
    private Map<Long, String> buildAuthorNameMap(List<DocumentStatistics> docs) {
        List<Long> authorIds = docs.stream()
                .map(DocumentStatistics::getAuthorId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        if (authorIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<UserStatistics> users = userMapper.selectBatchIds(authorIds);
        if (users == null || users.isEmpty()) {
            return Collections.emptyMap();
        }

        return users.stream()
                .filter(u -> u.getId() != null)
                .collect(Collectors.toMap(
                        UserStatistics::getId,
                        u -> {
                            String realName = u.getRealName();
                            return (realName != null && !realName.isEmpty()) ? realName : u.getUsername();
                        },
                        (a, b) -> a));
    }

    /**
     * 批量构建分类 ID → 分类名称映射
     */
    private Map<Long, String> buildCategoryNameMap(List<DocumentStatistics> docs) {
        List<Long> categoryIds = docs.stream()
                .map(DocumentStatistics::getCategoryId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        if (categoryIds.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, String> nameMap = new HashMap<>();
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT id, category_name FROM kb_category WHERE deleted = 0");
            if (rows != null) {
                for (Map<String, Object> row : rows) {
                    Long id = toLong(row.get("id"));
                    String name = (String) row.get("category_name");
                    if (id != null && categoryIds.contains(id)) {
                        nameMap.put(id, name != null ? name : "未命名");
                    }
                }
            }
        } catch (Exception e) {
            log.warn("构建分类名称映射失败：{}", e.getMessage());
        }
        return nameMap;
    }

    /**
     * 批量构建文档 ID → 摘要映射
     */
    private Map<Long, String> buildSummaryMap(List<DocumentStatistics> docs) {
        List<Long> docIds = docs.stream()
                .map(DocumentStatistics::getId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        if (docIds.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, String> summaryMap = new HashMap<>();
        try {
            // 构建 IN 查询占位符
            String placeholders = docIds.stream().map(id -> "?").collect(Collectors.joining(","));
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT id, summary FROM kb_document WHERE deleted = 0 AND id IN (" + placeholders + ")",
                    docIds.toArray());
            if (rows != null) {
                for (Map<String, Object> row : rows) {
                    Long id = toLong(row.get("id"));
                    String summary = (String) row.get("summary");
                    if (id != null) {
                        summaryMap.put(id, summary != null ? summary : "");
                    }
                }
            }
        } catch (Exception e) {
            log.warn("构建文档摘要映射失败：{}", e.getMessage());
        }
        return summaryMap;
    }

    private long nullSafe(Long value) {
        return value != null ? value : 0L;
    }

    private Long toLong(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Number) return ((Number) obj).longValue();
        try {
            return Long.parseLong(obj.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
