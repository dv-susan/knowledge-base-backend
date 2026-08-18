package com.knowledge.base.statistics.task;

import com.knowledge.base.common.utils.SnowflakeIdGenerator;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 统计聚合定时任务
 *
 * <p>每日定时将 kb_view_history 原始数据聚合写入预聚合表，
 * 减少实时查询的计算开销，提升统计接口响应速度。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Component
public class StatisticsAggregationTask {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private static final String REDIS_VIEW_COUNTER_PREFIX = "stats:counter:view:";
    private static final String REDIS_LIKE_COUNTER_PREFIX = "stats:counter:like:";
    private static final String REDIS_COMMENT_COUNTER_PREFIX = "stats:counter:comment:";

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 每日文档统计聚合
     *
     * <p>每天 00:05 执行，聚合前一天的浏览/点赞/评论数据写入 kb_document_statistics</p>
     */
    @Scheduled(cron = "0 5 0 * * ?")
    public void aggregateDailyDocumentStatistics() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String dateStr = yesterday.format(DATE_FMT);
        log.info("开始执行每日文档统计聚合：date={}", dateStr);

        try {
            // 查询前一天的浏览数据，按文档聚合
            String querySql = "SELECT document_id, MAX(document_title) AS document_title, COUNT(*) AS view_count " +
                    "FROM kb_view_history " +
                    "WHERE DATE(created_at) = ? " +
                    "GROUP BY document_id";

            List<Map<String, Object>> rows = jdbcTemplate.queryForList(querySql, dateStr);

            if (rows.isEmpty()) {
                log.info("无文档浏览数据需要聚合：date={}", dateStr);
                return;
            }

            // 批量 INSERT ... ON DUPLICATE KEY UPDATE
            String insertSql = "INSERT INTO kb_document_statistics " +
                    "(id, document_id, document_title, view_count, like_count, comment_count, stat_date, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, NOW()) " +
                    "ON DUPLICATE KEY UPDATE view_count = VALUES(view_count), " +
                    "like_count = VALUES(like_count), comment_count = VALUES(comment_count)";

            List<Object[]> batchArgs = new ArrayList<>();
            for (Map<String, Object> row : rows) {
                Long documentId = toLong(row.get("document_id"));
                String documentTitle = (String) row.get("document_title");
                long viewCount = toLong(row.get("view_count"));

                // 从 Redis 读取点赞和评论计数
                long likeCount = getRedisCounter(REDIS_LIKE_COUNTER_PREFIX, documentId, yesterday);
                long commentCount = getRedisCounter(REDIS_COMMENT_COUNTER_PREFIX, documentId, yesterday);

                batchArgs.add(new Object[]{
                        SnowflakeIdGenerator.getInstance().nextId(),
                        documentId,
                        documentTitle,
                        viewCount,
                        likeCount,
                        commentCount,
                        dateStr
                });
            }

            jdbcTemplate.batchUpdate(insertSql, batchArgs);
            log.info("每日文档统计聚合完成：date={}, 文档数={}", dateStr, rows.size());
        } catch (Exception e) {
            log.error("每日文档统计聚合失败：date={}, error={}", dateStr, e.getMessage(), e);
        }
    }

    /**
     * 每日用户统计聚合
     *
     * <p>每天 00:10 执行，聚合前一天的用户浏览数据写入 kb_user_statistics</p>
     */
    @Scheduled(cron = "0 10 0 * * ?")
    public void aggregateDailyUserStatistics() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String dateStr = yesterday.format(DATE_FMT);
        log.info("开始执行每日用户统计聚合：date={}", dateStr);

        try {
            // 查询前一天的浏览数据，按用户聚合
            String querySql = "SELECT user_id, MAX(user_name) AS user_name, COUNT(*) AS view_count " +
                    "FROM kb_view_history " +
                    "WHERE DATE(created_at) = ? AND user_id IS NOT NULL " +
                    "GROUP BY user_id";

            List<Map<String, Object>> rows = jdbcTemplate.queryForList(querySql, dateStr);

            if (rows.isEmpty()) {
                log.info("无用户浏览数据需要聚合：date={}", dateStr);
                return;
            }

            // 批量 INSERT ... ON DUPLICATE KEY UPDATE
            String insertSql = "INSERT INTO kb_user_statistics " +
                    "(id, user_id, user_name, document_count, comment_count, like_count, view_count, login_count, stat_date, created_at) " +
                    "VALUES (?, ?, ?, 0, 0, 0, ?, 0, ?, NOW()) " +
                    "ON DUPLICATE KEY UPDATE view_count = VALUES(view_count)";

            List<Object[]> batchArgs = new ArrayList<>();
            for (Map<String, Object> row : rows) {
                Long userId = toLong(row.get("user_id"));
                String userName = (String) row.get("user_name");
                long viewCount = toLong(row.get("view_count"));

                batchArgs.add(new Object[]{
                        SnowflakeIdGenerator.getInstance().nextId(),
                        userId,
                        userName,
                        viewCount,
                        dateStr
                });
            }

            jdbcTemplate.batchUpdate(insertSql, batchArgs);
            log.info("每日用户统计聚合完成：date={}, 用户数={}", dateStr, rows.size());
        } catch (Exception e) {
            log.error("每日用户统计聚合失败：date={}, error={}", dateStr, e.getMessage(), e);
        }
    }

    /**
     * 清理过期浏览历史记录
     *
     * <p>每天 02:30 执行，删除 90 天前的浏览历史记录，控制表数据量</p>
     */
    @Scheduled(cron = "0 30 2 * * ?")
    public void cleanupOldViewHistory() {
        log.info("开始清理过期浏览历史记录（保留90天）");

        try {
            int deleted = jdbcTemplate.update(
                    "DELETE FROM kb_view_history WHERE created_at < DATE_SUB(NOW(), INTERVAL 90 DAY)");
            log.info("过期浏览历史记录清理完成：删除 {} 条", deleted);
        } catch (Exception e) {
            log.error("清理过期浏览历史记录失败：error={}", e.getMessage(), e);
        }
    }

    /**
     * 从 Redis 读取计数器值
     *
     * @param prefix      Redis key 前缀
     * @param documentId  文档ID
     * @param date        日期
     * @return 计数值，读取失败返回 0
     */
    private long getRedisCounter(String prefix, Long documentId, LocalDate date) {
        try {
            String key = prefix + documentId + ":" + date.format(DATE_FMT);
            Object value = redisTemplate.opsForValue().get(key);
            if (value instanceof Number) {
                return ((Number) value).longValue();
            }
            if (value instanceof String) {
                return Long.parseLong((String) value);
            }
        } catch (Exception e) {
            log.debug("读取 Redis 计数器失败：prefix={}, docId={}, date={}", prefix, documentId, date);
        }
        return 0;
    }

    private long toLong(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
