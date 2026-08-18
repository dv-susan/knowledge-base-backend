package com.knowledge.base.statistics.mq;

import com.knowledge.base.common.utils.SnowflakeIdGenerator;
import com.knowledge.base.common.event.StatisticsEventDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

/**
 * 统计事件 MQ 监听器
 *
 * <p>消费来自业务服务的统计事件，写入 kb_view_history 表和 Redis 计数器</p>
 * <p>队列名通过 SpEL 表达式动态引用实例隔离的 Queue Bean</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Component
public class StatisticsMQListener {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private static final String REDIS_VIEW_COUNTER_PREFIX = "stats:counter:view:";
    private static final String REDIS_LIKE_COUNTER_PREFIX = "stats:counter:like:";
    private static final String REDIS_COMMENT_COUNTER_PREFIX = "stats:counter:comment:";

    /**
     * 消费浏览事件
     */
    @RabbitListener(queues = "#{@statisticsViewQueue.name}")
    public void handleViewEvent(StatisticsEventDTO event) {
        try {
            log.debug("收到浏览事件：userId={}, documentId={}, title={}",
                    event.getUserId(), event.getDocumentId(), event.getDocumentTitle());

            // 写入浏览历史记录表
            long recordId = SnowflakeIdGenerator.getInstance().nextId();
            jdbcTemplate.update(
                    "INSERT INTO kb_view_history (id, user_id, user_name, document_id, document_title, " +
                            "ip_address, user_agent, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())",
                    recordId,
                    event.getUserId(),
                    event.getUserName(),
                    event.getDocumentId(),
                    event.getDocumentTitle(),
                    event.getIpAddress(),
                    event.getUserAgent()
            );

            // 更新 Redis 实时计数器
            String todayKey = REDIS_VIEW_COUNTER_PREFIX + event.getDocumentId() + ":" + LocalDate.now();
            redisTemplate.opsForValue().increment(todayKey);
            redisTemplate.expire(todayKey, 48, TimeUnit.HOURS);

            log.debug("浏览事件处理完成：recordId={}", recordId);
        } catch (Exception e) {
            log.error("处理浏览事件失败：userId={}, documentId={}, error={}",
                    event.getUserId(), event.getDocumentId(), e.getMessage(), e);
            // 不抛出异常，避免消息无限重试
        }
    }

    /**
     * 消费点赞事件
     */
    @RabbitListener(queues = "#{@statisticsLikeQueue.name}")
    public void handleLikeEvent(StatisticsEventDTO event) {
        try {
            log.debug("收到点赞事件：userId={}, documentId={}", event.getUserId(), event.getDocumentId());

            // 更新 Redis 实时点赞计数器
            String todayKey = REDIS_LIKE_COUNTER_PREFIX + event.getDocumentId() + ":" + LocalDate.now();
            redisTemplate.opsForValue().increment(todayKey);
            redisTemplate.expire(todayKey, 48, TimeUnit.HOURS);

            log.debug("点赞事件处理完成：documentId={}", event.getDocumentId());
        } catch (Exception e) {
            log.error("处理点赞事件失败：documentId={}, error={}",
                    event.getDocumentId(), e.getMessage(), e);
        }
    }

    /**
     * 消费评论事件
     */
    @RabbitListener(queues = "#{@statisticsCommentQueue.name}")
    public void handleCommentEvent(StatisticsEventDTO event) {
        try {
            log.debug("收到评论事件：userId={}, documentId={}", event.getUserId(), event.getDocumentId());

            // 更新 Redis 实时评论计数器
            String todayKey = REDIS_COMMENT_COUNTER_PREFIX + event.getDocumentId() + ":" + LocalDate.now();
            redisTemplate.opsForValue().increment(todayKey);
            redisTemplate.expire(todayKey, 48, TimeUnit.HOURS);

            log.debug("评论事件处理完成：documentId={}", event.getDocumentId());
        } catch (Exception e) {
            log.error("处理评论事件失败：documentId={}, error={}",
                    event.getDocumentId(), e.getMessage(), e);
        }
    }
}
