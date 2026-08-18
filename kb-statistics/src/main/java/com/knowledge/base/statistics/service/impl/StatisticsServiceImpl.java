package com.knowledge.base.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.knowledge.base.statistics.entity.CommentStatistics;
import com.knowledge.base.statistics.entity.DocumentStatistics;
import com.knowledge.base.statistics.entity.UserStatistics;
import com.knowledge.base.statistics.entity.ViewStatistics;
import com.knowledge.base.statistics.mapper.AiStatisticsMapper;
import com.knowledge.base.statistics.mapper.CommentStatisticsMapper;
import com.knowledge.base.statistics.mapper.DocumentStatisticsMapper;
import com.knowledge.base.statistics.mapper.DocumentStatisticsAggMapper;
import com.knowledge.base.statistics.mapper.UserStatisticsMapper;
import com.knowledge.base.statistics.mapper.UserStatisticsAggMapper;
import com.knowledge.base.statistics.mapper.ViewStatisticsMapper;
import com.knowledge.base.statistics.model.DailyCount;
import com.knowledge.base.statistics.model.IdCount;
import com.knowledge.base.statistics.service.StatisticsService;
import com.knowledge.base.statistics.vo.ActiveUserVO;
import com.knowledge.base.statistics.vo.AdminOverviewVO;
import com.knowledge.base.statistics.vo.CategoryDistributionVO;
import com.knowledge.base.statistics.vo.DashboardVO;
import com.knowledge.base.statistics.vo.HotDocumentVO;
import com.knowledge.base.statistics.vo.OverviewVO;
import com.knowledge.base.statistics.vo.TrendVO;
import com.knowledge.base.statistics.vo.UserActivityVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 统计分析Service实现
 *
 * <p>企业级实现标准：
 * <ul>
 *   <li>热点数据使用 Redis 缓存，减少 DB 压力</li>
 *   <li>批量聚合查询替代 N+1 单条查询</li>
 *   <li>XML Mapper 预定义查询方法均完整对接</li>
 *   <li>缓存穿透通过 Cacheable + unless 条件防空</li>
 * </ul>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Resource
    private DocumentStatisticsMapper documentMapper;

    @Resource
    private UserStatisticsMapper userMapper;

    @Resource
    private CommentStatisticsMapper commentMapper;

    @Resource
    private ViewStatisticsMapper viewMapper;

    @Resource
    private DocumentStatisticsAggMapper documentStatisticsAggMapper;

    @Resource
    private UserStatisticsAggMapper userStatisticsAggMapper;

    @Resource
    private AiStatisticsMapper aiStatisticsMapper;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource(name = "caffeineCacheManager")
    private CacheManager caffeineCacheManager;

    // ======================== 本地内存缓存 ========================

    /** 首页数据概览缓存：10 分钟自动过期，减少 DB 查询 */
    private final LoadingCache<String, OverviewVO> overviewCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(10))
            .maximumSize(1)
            .build(key -> loadOverview());

    // ======================== 缓存 Key 常量 ========================
    private static final String CACHE_COMPOSITE_REDIS_KEY = "stats:hotDocuments:top6";
    private static final String CACHE_COMPOSITE_CAFFEINE_NAME = "hotDocuments";
    private static final String CACHE_COMPOSITE_CAFFEINE_KEY = "top6";
    private static final String CACHE_LATEST_REDIS_KEY = "stats:latestDocuments:top6";
    private static final String CACHE_LATEST_CAFFEINE_NAME = "latestDocuments";
    private static final String CACHE_LATEST_CAFFEINE_KEY = "top6";

    private static final String CACHE_OVERVIEW = "stats:overview";
    private static final String CACHE_ADMIN_OVERVIEW = "stats:adminOverview";
    private static final String CACHE_DASHBOARD = "stats:dashboard";
    private static final String CACHE_CATEGORY_DIST = "stats:categoryDistribution";
    private static final String CACHE_HOT_DOCS = "stats:hotDocuments";
    private static final String CACHE_ACTIVE_USERS = "stats:activeUsers";
    private static final String CACHE_DOC_TREND = "stats:documentTrend";
    private static final String CACHE_USER_ACTIVITY = "stats:userActivity";

    // ======================== 业务常量 ========================

    /** 热门文档默认返回数量 */
    private static final int DEFAULT_HOT_LIMIT = 10;
    /** 活跃用户默认返回数量 */
    private static final int DEFAULT_ACTIVE_USER_LIMIT = 10;
    /** 最大返回数量上限 */
    private static final int MAX_LIMIT = 100;
    /** 默认活动评分：创建文档 +10, 评论 +5, 浏览 +1 */
    private static final int SCORE_DOCUMENT = 10;
    private static final int SCORE_COMMENT = 5;
    private static final int SCORE_VIEW = 1;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** 热门文档复合评分权重：浏览 1，点赞 3，收藏 5 */
    private static final int HOT_SCORE_LIKE = 3;
    private static final int HOT_SCORE_FAVORITE = 5;

    // ======================== 数据概览 ========================

    @Override
    public OverviewVO getOverview() {
        return overviewCache.get("overview");
    }

    /**
     * 从数据库加载数据概览（仅在缓存过期时调用）
     */
    private OverviewVO loadOverview() {
        log.debug("查询数据概览（内存缓存未命中，10分钟刷新一次）");

        OverviewVO overview = new OverviewVO();
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        overview.setTotalDocuments(nullSafe(documentMapper.countAll()));
        overview.setTotalUsers(nullSafe(userMapper.countAll()));
        overview.setTodayDocuments(nullSafe(documentMapper.countByDateRange(todayStart, todayEnd)));
        overview.setTodayUsers(nullSafe(userMapper.countByDateRange(todayStart, todayEnd)));
        overview.setTotalViews(nullSafe(viewMapper.countAll()));
        overview.setTodayViews(nullSafe(viewMapper.countByDateRange(todayStart, todayEnd)));
        overview.setTotalLikes(nullSafe(documentMapper.sumLikeCount()));
        overview.setTotalFavorites(nullSafe(documentMapper.sumFavoriteCount()));
        overview.setTotalComments(nullSafe(commentMapper.selectCount(null)));
        overview.setPendingReviews(nullSafe(documentMapper.countByStatus(0)));
        overview.setAiSearchCount(nullSafe(aiStatisticsMapper.countConversations()));
        overview.setAiQaCount(nullSafe(aiStatisticsMapper.countUserMessages()));
        overview.setActiveUserCount(nullSafe(viewMapper.countActiveUsers(LocalDateTime.now().minusDays(30))));

        return overview;
    }

    // ======================== 文档趋势 ========================

    @Override
    @Cacheable(value = CACHE_DOC_TREND, key = "#startDate + '_' + #endDate + '_' + #type",
            unless = "#result == null || #result.isEmpty()")
    public List<TrendVO> getDocumentTrend(LocalDate startDate, LocalDate endDate, String type) {
        log.debug("查询文档趋势：startDate={}, endDate={}, type={}", startDate, endDate, type);

        LocalDateTime start = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(endDate, LocalTime.MAX);

        // 使用 XML Mapper 的 GROUP BY 批量查询，一次 SQL 取所有日期
        List<DailyCount> dailyCounts = queryDailyCounts(type, start, end);
        Map<String, Long> dateCountMap = toDateCountMap(dailyCounts);

        // 填充日期范围内的每一天（含无数据的日期补 0）
        List<TrendVO> trends = new ArrayList<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            String dateStr = current.toString();
            trends.add(TrendVO.builder()
                    .date(dateStr)
                    .count(dateCountMap.getOrDefault(dateStr, 0L))
                    .build());
            current = current.plusDays(1);
        }

        return trends;
    }

    /**
     * 根据类型查询每日统计数据
     */
    private List<DailyCount> queryDailyCounts(String type, LocalDateTime start, LocalDateTime end) {
        LocalDate startDate = start.toLocalDate();
        LocalDate endDate = end.toLocalDate();

        switch (type) {
            case "create":
                return documentMapper.countDailyDocuments(start, end);
            case "view":
                return documentStatisticsAggMapper.countDailyViews(startDate, endDate);
            case "like":
            case "favorite":
                log.debug("趋势类型 {} 暂不支持按天聚合", type);
                return Collections.emptyList();
            default:
                log.warn("未知趋势类型：{}", type);
                return Collections.emptyList();
        }
    }

    /**
     * 将 DailyCount 列表转为 { date → count } 映射
     */
    private Map<String, Long> toDateCountMap(List<DailyCount> dailyCounts) {
        if (dailyCounts == null || dailyCounts.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Long> map = new LinkedHashMap<>();
        for (DailyCount dc : dailyCounts) {
            if (dc.getDate() != null && dc.getCount() != null) {
                map.put(dc.getDate(), dc.getCount());
            }
        }
        return map;
    }

    // ======================== 用户活跃度 ========================

    @Override
    @Cacheable(value = CACHE_USER_ACTIVITY,
            key = "#startDate + '_' + #endDate",
            unless = "#result == null || #result.isEmpty()")
    public List<UserActivityVO> getUserActivity(LocalDate startDate, LocalDate endDate) {
        log.debug("查询用户活跃度：startDate={}, endDate={}", startDate, endDate);

        LocalDateTime start = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(endDate, LocalTime.MAX);

        // 获取所有用户
        List<UserStatistics> users = userMapper.selectList(null);
        if (users == null || users.isEmpty()) {
            return Collections.emptyList();
        }

        // 批量预取各用户的数据计数（避免 N+1）
        Map<Long, Long> docCountMap = buildDocCountMap(start, end);
        Map<Long, Long> commentCountMap = buildCommentCountMap(start, end);
        Map<Long, Long> viewCountMap = buildViewCountMap(start, end);

        return users.stream()
                .map(user -> {
                    Long userId = user.getId();
                    long docCount = docCountMap.getOrDefault(userId, 0L);
                    long comCount = commentCountMap.getOrDefault(userId, 0L);
                    long vCount = viewCountMap.getOrDefault(userId, 0L);

                    return UserActivityVO.builder()
                            .userId(userId)
                            .username(user.getUsername())
                            .documentCount(docCount)
                            .commentCount(comCount)
                            .viewCount(vCount)
                            .activityScore(calcActivityScore(docCount, comCount, vCount))
                            .build();
                })
                .sorted(Comparator.comparing(UserActivityVO::getActivityScore).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 构建用户→文档数映射
     */
    private Map<Long, Long> buildDocCountMap(LocalDateTime start, LocalDateTime end) {
        List<DocumentStatistics> docs = documentMapper.selectList(
                new LambdaQueryWrapper<DocumentStatistics>()
                        .between(DocumentStatistics::getCreatedAt, start, end)
                        .select(DocumentStatistics::getAuthorId));
        return docs.stream()
                .filter(d -> d.getAuthorId() != null)
                .collect(Collectors.groupingBy(DocumentStatistics::getAuthorId, Collectors.counting()));
    }

    /**
     * 构建用户→评论数映射
     */
    private Map<Long, Long> buildCommentCountMap(LocalDateTime start, LocalDateTime end) {
        List<CommentStatistics> comments = commentMapper.selectList(
                new LambdaQueryWrapper<CommentStatistics>()
                        .between(CommentStatistics::getCreatedAt, start, end)
                        .select(CommentStatistics::getUserId));
        return comments.stream()
                .filter(c -> c.getUserId() != null)
                .collect(Collectors.groupingBy(CommentStatistics::getUserId, Collectors.counting()));
    }

    /**
     * 构建用户→浏览数映射
     */
    private Map<Long, Long> buildViewCountMap(LocalDateTime start, LocalDateTime end) {
        List<ViewStatistics> views = viewMapper.selectList(
                new LambdaQueryWrapper<ViewStatistics>()
                        .between(ViewStatistics::getCreatedAt, start, end)
                        .select(ViewStatistics::getUserId));
        return views.stream()
                .filter(v -> v.getUserId() != null)
                .collect(Collectors.groupingBy(ViewStatistics::getUserId, Collectors.counting()));
    }

    /**
     * 计算用户活跃度评分
     */
    private double calcActivityScore(long docCount, long commentCount, long viewCount) {
        return (docCount * SCORE_DOCUMENT + commentCount * SCORE_COMMENT + viewCount * SCORE_VIEW) / 10.0;
    }

    // ======================== 分类分布（P0：Mock → 真实查询） ========================

    @Override
    @Cacheable(value = CACHE_CATEGORY_DIST, key = "'distribution'",
            unless = "#result == null || #result.isEmpty()")
    public List<CategoryDistributionVO> getCategoryDistribution() {
        log.debug("查询分类分布（缓存未命中）");

        // 使用 XML Mapper 预定义的 countByCategory 一次查询
        List<IdCount> categoryCounts = documentMapper.countByCategory();
        if (categoryCounts == null || categoryCounts.isEmpty()) {
            return Collections.emptyList();
        }

        // 计算总文档数用于百分比
        long total = categoryCounts.stream()
                .mapToLong(ic -> nullSafe(ic.getCount()))
                .sum();
        if (total <= 0) {
            return Collections.emptyList();
        }

        // 获取分类名称映射
        Map<Long, String> categoryNameMap = buildCategoryNameMap();

        return categoryCounts.stream()
                .map(ic -> {
                    Long categoryId = ic.getId();
                    long count = nullSafe(ic.getCount());
                    String categoryName = categoryId != null
                            ? categoryNameMap.getOrDefault(categoryId, "未命名")
                            : "未分类";

                    return CategoryDistributionVO.builder()
                            .categoryId(categoryId)
                            .categoryName(categoryName)
                            .documentCount(count)
                            .percentage(calcPercentage(count, total))
                            .build();
                })
                .sorted(Comparator.comparing(CategoryDistributionVO::getDocumentCount).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 从 kb_category 视图查询真实分类名称映射
     */
    private Map<Long, String> buildCategoryNameMap() {
        Map<Long, String> nameMap = new HashMap<>();
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT id, category_name FROM kb_category WHERE deleted = 0");
            if (rows != null) {
                for (Map<String, Object> row : rows) {
                    Long id = toLong(row.get("id"));
                    String name = (String) row.get("category_name");
                    if (id != null) {
                        nameMap.put(id, name != null ? name : "未命名");
                    }
                }
            }
        } catch (Exception e) {
            log.warn("构建分类名称映射失败：{}", e.getMessage());
        }
        return nameMap;
    }

    // ======================== 热门文档（P0：Mock → 真实查询） ========================

    @Override
    @Cacheable(value = CACHE_HOT_DOCS, key = "#type + '_' + #size",
            unless = "#result == null || #result.isEmpty()",
            condition = "!'composite'.equals(#type)")
    public List<HotDocumentVO> getHotDocuments(String type, Integer size) {
        log.debug("查询热门文档：type={}, size={}", type, size);

        int limit = Math.min(size != null ? size : DEFAULT_HOT_LIMIT, MAX_LIMIT);

        // 复合热度类型：走 L1 Caffeine → L2 Redis → DB 的三层读取链路
        if ("composite".equals(type)) {
            return getCompositeHotDocuments(limit);
        }

        List<DocumentStatistics> docs = queryHotDocsByType(type, limit);

        if (docs == null || docs.isEmpty()) {
            return Collections.emptyList();
        }

        return docs.stream()
                .map(doc -> {
                    long statsValue = extractStatsValue(doc, type);
                    return HotDocumentVO.builder()
                            .documentId(doc.getId())
                            .title(doc.getTitle())
                            .authorId(doc.getAuthorId())
                            .authorName("") // 后续可从用户表联查
                            .categoryId(doc.getCategoryId())
                            .categoryName("") // 后续可从分类表联查
                            .viewCount(nullSafe(doc.getViewCount()))
                            .likeCount(nullSafe(doc.getLikeCount()))
                            .favoriteCount(nullSafe(doc.getFavoriteCount()))
                            .commentCount(0L) // 暂不联查评论数
                            .statisticsValue(statsValue)
                            .build();
                })
                .sorted(Comparator.comparing(HotDocumentVO::getStatisticsValue).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 根据类型查询热门文档
     */
    private List<DocumentStatistics> queryHotDocsByType(String type, int limit) {
        if ("like".equalsIgnoreCase(type)) {
            return documentMapper.selectMostLikedDocuments(limit);
        }
        if ("favorite".equalsIgnoreCase(type)) {
            return documentMapper.selectMostFavoritedDocuments(limit);
        }
        // 默认按浏览量
        return documentMapper.selectMostViewedDocuments(limit);
    }

    /**
     * 从文档记录中提取对应排序类型的统计值
     */
    private long extractStatsValue(DocumentStatistics doc, String type) {
        if ("like".equalsIgnoreCase(type)) {
            return nullSafe(doc.getLikeCount());
        }
        if ("favorite".equalsIgnoreCase(type)) {
            return nullSafe(doc.getFavoriteCount());
        }
        return nullSafe(doc.getViewCount());
    }

    // ======================== 复合热度文档（定时任务预计算 + 多层缓存） ========================

    /**
     * 获取复合热度排行榜文档
     *
     * <p>读取链路：Caffeine (L1) → Redis (L2) → DB 实时计算 (兜底)</p>
     * <p>数据由 {@code HotDocumentsCacheTask} 每30分钟预计算并写入缓存</p>
     */
    private List<HotDocumentVO> getCompositeHotDocuments(int limit) {
        // 1. 检查 Caffeine 本地缓存 (L1)
        Cache caffeineCache = caffeineCacheManager.getCache(CACHE_COMPOSITE_CAFFEINE_NAME);
        if (caffeineCache != null) {
            Cache.ValueWrapper wrapper = caffeineCache.get(CACHE_COMPOSITE_CAFFEINE_KEY);
            if (wrapper != null) {
                @SuppressWarnings("unchecked")
                List<HotDocumentVO> cached = (List<HotDocumentVO>) wrapper.get();
                if (cached != null && !cached.isEmpty()) {
                    log.debug("复合热度文档命中 Caffeine 本地缓存，共 {} 条", cached.size());
                    return cached.size() > limit ? cached.subList(0, limit) : cached;
                }
            }
        }

        // 2. 检查 Redis 缓存 (L2)
        try {
            @SuppressWarnings("unchecked")
            List<HotDocumentVO> redisCached = (List<HotDocumentVO>) redisTemplate.opsForValue()
                    .get(CACHE_COMPOSITE_REDIS_KEY);
            if (redisCached != null && !redisCached.isEmpty()) {
                log.debug("复合热度文档命中 Redis 缓存，共 {} 条", redisCached.size());
                return redisCached.size() > limit ? redisCached.subList(0, limit) : redisCached;
            }
        } catch (Exception e) {
            log.warn("读取 Redis 复合热度缓存失败：{}", e.getMessage());
        }

        // 3. 兜底：实时从数据库计算
        log.info("复合热度文档缓存未命中，执行实时数据库查询");
        return computeCompositeOnDemand(limit);
    }

    /**
     * 实时从数据库计算复合热度 Top N 文档（缓存兜底逻辑）
     *
     * <p>热度公式：viewCount * 1 + likeCount * 3 + favoriteCount * 5</p>
     */
    private List<HotDocumentVO> computeCompositeOnDemand(int limit) {
        List<DocumentStatistics> allDocs = documentMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<DocumentStatistics>()
                        .eq(DocumentStatistics::getStatus, 1)
                        .eq(DocumentStatistics::getDeleted, 0)
                        .gt(DocumentStatistics::getViewCount, 0)
                        .orderByDesc(DocumentStatistics::getViewCount)
                        .last("LIMIT 500"));

        if (allDocs == null || allDocs.isEmpty()) {
            return Collections.emptyList();
        }

        List<DocumentStatistics> topDocs = allDocs.stream()
                .sorted((a, b) -> Long.compare(
                        compositeScore(b), compositeScore(a)))
                .limit(limit)
                .collect(Collectors.toList());

        // 批量查询摘要
        Map<Long, String> summaryMap = buildSummaryMapForDocs(topDocs);

        return topDocs.stream()
                .map(doc -> HotDocumentVO.builder()
                        .documentId(doc.getId())
                        .title(doc.getTitle())
                        .authorId(doc.getAuthorId())
                        .authorName("")
                        .categoryId(doc.getCategoryId())
                        .categoryName("")
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
     * 批量构建文档 ID → 摘要映射
     */
    private Map<Long, String> buildSummaryMapForDocs(List<DocumentStatistics> docs) {
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

    private long compositeScore(DocumentStatistics doc) {
        return nullSafe(doc.getViewCount()) * SCORE_VIEW
                + nullSafe(doc.getLikeCount()) * HOT_SCORE_LIKE
                + nullSafe(doc.getFavoriteCount()) * HOT_SCORE_FAVORITE;
    }

    // ======================== 最新文档（定时任务预计算 + 多层缓存） ========================

    @Override
    public List<HotDocumentVO> getLatestDocuments(Integer size) {
        int limit = Math.min(size != null ? size : 6, MAX_LIMIT);

        // 1. 检查 Caffeine 本地缓存 (L1)
        Cache caffeineCache = caffeineCacheManager.getCache(CACHE_LATEST_CAFFEINE_NAME);
        if (caffeineCache != null) {
            Cache.ValueWrapper wrapper = caffeineCache.get(CACHE_LATEST_CAFFEINE_KEY);
            if (wrapper != null) {
                @SuppressWarnings("unchecked")
                List<HotDocumentVO> cached = (List<HotDocumentVO>) wrapper.get();
                if (cached != null && !cached.isEmpty()) {
                    log.debug("最新文档命中 Caffeine 本地缓存，共 {} 条", cached.size());
                    return cached.size() > limit ? cached.subList(0, limit) : cached;
                }
            }
        }

        // 2. 检查 Redis 缓存 (L2)
        try {
            @SuppressWarnings("unchecked")
            List<HotDocumentVO> redisCached = (List<HotDocumentVO>) redisTemplate.opsForValue()
                    .get(CACHE_LATEST_REDIS_KEY);
            if (redisCached != null && !redisCached.isEmpty()) {
                log.debug("最新文档命中 Redis 缓存，共 {} 条", redisCached.size());
                return redisCached.size() > limit ? redisCached.subList(0, limit) : redisCached;
            }
        } catch (Exception e) {
            log.warn("读取 Redis 最新文档缓存失败：{}", e.getMessage());
        }

        // 3. 兜底：实时从数据库查询
        log.info("最新文档缓存未命中，执行实时数据库查询");
        return computeLatestOnDemand(limit);
    }

    private List<HotDocumentVO> computeLatestOnDemand(int limit) {
        List<DocumentStatistics> latestDocs = documentMapper.selectList(
                new LambdaQueryWrapper<DocumentStatistics>()
                        .eq(DocumentStatistics::getStatus, 1)
                        .eq(DocumentStatistics::getDeleted, 0)
                        .orderByDesc(DocumentStatistics::getCreatedAt)
                        .last("LIMIT " + limit));

        if (latestDocs == null || latestDocs.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, String> summaryMap = buildSummaryMapForDocs(latestDocs);

        return latestDocs.stream()
                .map(doc -> HotDocumentVO.builder()
                        .documentId(doc.getId())
                        .title(doc.getTitle())
                        .authorId(doc.getAuthorId())
                        .authorName("")
                        .categoryId(doc.getCategoryId())
                        .categoryName("")
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

    // ======================== 活跃用户（P0：Mock → 真实查询） ========================

    @Override
    @Cacheable(value = CACHE_ACTIVE_USERS, key = "#type + '_' + #size",
            unless = "#result == null || #result.isEmpty()")
    public List<ActiveUserVO> getActiveUsers(String type, Integer size) {
        log.debug("查询活跃用户：type={}, size={}", type, size);

        int limit = Math.min(size != null ? size : DEFAULT_ACTIVE_USER_LIMIT, MAX_LIMIT);
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        // 根据类型使用不同的聚合数据源
        List<IdCount> topUsers = queryTopUsersByType(type, thirtyDaysAgo, limit);
        if (topUsers == null || topUsers.isEmpty()) {
            return Collections.emptyList();
        }

        // 批量获取用户信息
        List<Long> userIds = topUsers.stream()
                .map(IdCount::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        Map<Long, UserStatistics> userMap = buildUserMap(userIds);

        return topUsers.stream()
                .map(ic -> {
                    Long userId = ic.getId();
                    long statsValue = nullSafe(ic.getCount());
                    UserStatistics user = userMap.get(userId);

                    return ActiveUserVO.builder()
                            .userId(userId)
                            .username(user != null ? user.getUsername() : "未知用户")
                            .realName(user != null ? user.getRealName() : "")
                            .avatar(user != null ? user.getAvatar() : "")
                            .documentCount(0L)
                            .commentCount(0L)
                            .viewCount(0L)
                            .statisticsValue(statsValue)
                            .build();
                })
                .sorted(Comparator.comparing(ActiveUserVO::getStatisticsValue).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 根据类型查询热门用户列表
     */
    private List<IdCount> queryTopUsersByType(String type, LocalDateTime since, int limit) {
        if ("create".equalsIgnoreCase(type)) {
            // 按文档创建数统计最活跃的作者，使用 kb_document 视图
            return documentMapper.selectTopAuthors(limit);
        }
        if ("comment".equalsIgnoreCase(type)) {
            return commentMapper.selectTopCommenters(limit);
        }
        if ("view".equalsIgnoreCase(type)) {
            try {
                List<IdCount> result = userStatisticsAggMapper.selectTopActiveUsers(
                        since.toLocalDate(), LocalDate.now(), limit);
                if (result != null && !result.isEmpty()) {
                    return result;
                }
            } catch (Exception e) {
                log.warn("查询预聚合用户统计表失败，回退到 kb_view_history：{}", e.getMessage());
            }
            return viewMapper.selectMostActiveViewers(limit);
        }
        // 默认按文档创建数
        return documentMapper.selectTopAuthors(limit);
    }

    /**
     * 批量构建用户ID→用户信息映射
     */
    private Map<Long, UserStatistics> buildUserMap(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<UserStatistics> users = userMapper.selectBatchIds(userIds);
        if (users == null) {
            return Collections.emptyMap();
        }
        return users.stream()
                .filter(u -> u.getId() != null)
                .collect(Collectors.toMap(UserStatistics::getId, u -> u, (a, b) -> a));
    }

    // ======================== 管理后台概览 ========================

    @Override
    @Cacheable(value = CACHE_ADMIN_OVERVIEW, key = "'adminOverview'", unless = "#result == null")
    public AdminOverviewVO getAdminOverview() {
        log.debug("查询管理后台概览（缓存未命中）");

        AdminOverviewVO vo = new AdminOverviewVO();
        vo.setTotalDocuments(nullSafe(documentMapper.countAll()));
        vo.setTotalUsers(nullSafe(userMapper.countAll()));
        vo.setPendingReviews(nullSafe(documentMapper.countByStatus(0)));
        vo.setTotalComments(nullSafe(commentMapper.selectCount(null)));
        vo.setTotalViews(nullSafe(viewMapper.countAll()));
        vo.setTotalLikes(nullSafe(documentMapper.sumLikeCount()));
        vo.setTotalFavorites(nullSafe(documentMapper.sumFavoriteCount()));
        vo.setAiSearchCount(nullSafe(aiStatisticsMapper.countConversations()));
        vo.setAiQaCount(nullSafe(aiStatisticsMapper.countUserMessages()));
        vo.setSystemHealth(98.0); // TODO: 对接健康检查端点动态计算

        return vo;
    }

    // ======================== 仪表盘数据 ========================

    @Override
    @Cacheable(value = CACHE_DASHBOARD, key = "'dashboard'", unless = "#result == null")
    public DashboardVO getDashboardData() {
        log.debug("查询仪表盘数据（缓存未命中）");

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);

        return DashboardVO.builder()
                .overview(getOverview())
                .documentTrend(getDocumentTrend(startDate, endDate, "create"))
                .categoryDistribution(getCategoryDistribution())
                .hotDocuments(getHotDocuments("view", DEFAULT_HOT_LIMIT))
                .activeUsers(getActiveUsers("create", DEFAULT_ACTIVE_USER_LIMIT))
                .build();
    }

    // ======================== 工具方法 ========================

    /**
     * 安全转 Long，避免 null 和类型转换异常
     */
    private Long toLong(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Number) {
            return ((Number) obj).longValue();
        }
        try {
            return Long.parseLong(obj.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 安全的 null → 0L 转换
     */
    private long nullSafe(Long value) {
        return value != null ? value : 0L;
    }

    /**
     * 计算百分比，保留一位小数
     */
    private double calcPercentage(long count, long total) {
        if (total <= 0) {
            return 0.0;
        }
        return Math.round((double) count / total * 1000.0) / 10.0;
    }
}
