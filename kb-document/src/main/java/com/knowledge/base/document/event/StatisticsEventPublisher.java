package com.knowledge.base.document.event;

import com.knowledge.base.common.config.InstanceIdentifier;
import com.knowledge.base.common.event.StatisticsEventDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * 统计事件发布器
 *
 * <p>负责将业务操作事件异步发布到 RabbitMQ，供统计服务消费</p>
 * <p>路由键使用 InstanceIdentifier 进行实例隔离，确保本机产生的事件只被本机统计服务消费</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Component
public class StatisticsEventPublisher {

    /** 统计交换机（所有实例共享） */
    private static final String STATISTICS_EXCHANGE = "kb.statistics.exchange";

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private ThreadPoolTaskExecutor asyncTaskExecutor;

    @Resource
    private InstanceIdentifier instanceIdentifier;

    private String viewRoutingKey() {
        return "statistics.view." + instanceIdentifier.getId() + ".record";
    }

    private String likeRoutingKey() {
        return "statistics.like." + instanceIdentifier.getId() + ".record";
    }

    private String commentRoutingKey() {
        return "statistics.comment." + instanceIdentifier.getId() + ".record";
    }

    /**
     * 发布文档浏览事件（异步，不阻塞主流程）
     */
    public void publishViewEvent(Long userId, String userName, Long documentId, String documentTitle) {
        CompletableFuture.runAsync(() -> {
            try {
                StatisticsEventDTO event = buildEvent("VIEW", userId, userName, documentId, documentTitle);
                rabbitTemplate.convertAndSend(STATISTICS_EXCHANGE, viewRoutingKey(), event);
                log.debug("浏览事件发布成功：documentId={}", documentId);
            } catch (Exception e) {
                log.error("发布浏览事件失败：documentId={}, error={}", documentId, e.getMessage());
            }
        }, asyncTaskExecutor);
    }

    /**
     * 发布文档点赞事件（异步）
     */
    public void publishLikeEvent(Long userId, String userName, Long documentId, String documentTitle) {
        CompletableFuture.runAsync(() -> {
            try {
                StatisticsEventDTO event = buildEvent("LIKE", userId, userName, documentId, documentTitle);
                rabbitTemplate.convertAndSend(STATISTICS_EXCHANGE, likeRoutingKey(), event);
                log.debug("点赞事件发布成功：documentId={}", documentId);
            } catch (Exception e) {
                log.error("发布点赞事件失败：documentId={}, error={}", documentId, e.getMessage());
            }
        }, asyncTaskExecutor);
    }

    /**
     * 发布评论创建事件（异步）
     */
    public void publishCommentEvent(Long userId, String userName, Long documentId, String documentTitle) {
        CompletableFuture.runAsync(() -> {
            try {
                StatisticsEventDTO event = buildEvent("COMMENT", userId, userName, documentId, documentTitle);
                rabbitTemplate.convertAndSend(STATISTICS_EXCHANGE, commentRoutingKey(), event);
                log.debug("评论事件发布成功：documentId={}", documentId);
            } catch (Exception e) {
                log.error("发布评论事件失败：documentId={}, error={}", documentId, e.getMessage());
            }
        }, asyncTaskExecutor);
    }

    private StatisticsEventDTO buildEvent(String eventType, Long userId, String userName,
                                          Long documentId, String documentTitle) {
        return StatisticsEventDTO.builder()
                .eventType(eventType)
                .userId(userId)
                .userName(userName)
                .documentId(documentId)
                .documentTitle(documentTitle)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
