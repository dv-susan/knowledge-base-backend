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

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 最新文档缓存定时刷新任务
 *
 * <p>每5分钟查询最新发布的 Top 6 文档，
 * 写入 Redis + Caffeine 本地缓存。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Component
public class LatestDocumentsCacheTask {

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

    private static final String REDIS_KEY = "stats:latestDocuments:top6";
    private static final String CAFFEINE_CACHE_NAME = "latestDocuments";
    private static final String CAFFEINE_CACHE_KEY = "top6";
    private static final int TOP_N = 6;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 服务启动后立即执行一次刷新
     */
    @PostConstruct
    public void init() {
        log.info("LatestDocumentsCacheTask 初始化，开始首次最新文档缓存刷新...");
        refreshLatestDocuments();
    }

    /**
     * 每5分钟刷新最新文档缓存
     */
    @Scheduled(fixedRate = 300000)
    public void refreshLatestDocuments() {
        log.info("开始刷新最新文档缓存...");
        try {
            List<HotDocumentVO> latestDocs = computeLatestDocuments();

            if (latestDocs == null || latestDocs.isEmpty()) {
                log.warn("未找到任何文档，跳过缓存刷新");
                return;
            }

            // 写入 Redis (L2)
            redisTemplate.opsForValue().set(REDIS_KEY, latestDocs);
            log.debug("最新文档已写入 Redis: key={}, count={}", REDIS_KEY, latestDocs.size());

            // 写入 Caffeine 本地缓存 (L1)
            org.springframework.cache.Cache cache = caffeineCacheManager.getCache(CAFFEINE_CACHE_NAME);
            if (cache != null) {
                cache.put(CAFFEINE_CACHE_KEY, latestDocs);
                log.debug("最新文档已写入 Caffeine: cache={}, key={}", CAFFEINE_CACHE_NAME, CAFFEINE_CACHE_KEY);
            } else {
                log.warn("Caffeine 缓存 '{}' 不存在，请检查 CacheConfig 配置", CAFFEINE_CACHE_NAME);
            }

            log.info("最新文档缓存刷新完成：共 {} 条", latestDocs.size());
        } catch (Exception e) {
            log.error("刷新最新文档缓存失败", e);
        }
    }

    /**
     * 查询最新的 Top N 文档（按创建时间倒序）
     */
    List<HotDocumentVO> computeLatestDocuments() {
        List<DocumentStatistics> latestDocs = documentMapper.selectList(
                new LambdaQueryWrapper<DocumentStatistics>()
                        .eq(DocumentStatistics::getStatus, 1)
                        .eq(DocumentStatistics::getDeleted, 0)
                        .orderByDesc(DocumentStatistics::getCreatedAt)
                        .last("LIMIT " + TOP_N));

        if (latestDocs == null || latestDocs.isEmpty()) {
            return Collections.emptyList();
        }

        // 批量获取作者名称
        Map<Long, String> authorNameMap = buildAuthorNameMap(latestDocs);

        // 批量获取分类名称
        Map<Long, String> categoryNameMap = buildCategoryNameMap(latestDocs);

        // 批量获取文档摘要
        Map<Long, String> summaryMap = buildSummaryMap(latestDocs);

        return latestDocs.stream()
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
                        .createdAt(doc.getCreatedAt() != null ? doc.getCreatedAt().format(DATE_FMT) : "")
                        .statisticsValue(0L)
                        .build())
                .collect(Collectors.toList());
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
