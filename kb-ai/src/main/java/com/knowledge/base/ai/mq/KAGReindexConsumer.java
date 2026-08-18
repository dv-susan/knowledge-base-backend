package com.knowledge.base.ai.mq;

import com.knowledge.base.ai.rag.kag.graph.GraphBuildService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * KAG 图谱构建消费者
 *
 * <p>监听 KAG 队列，消费图谱构建任务，调用 GraphBuildService 执行构建。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KAGReindexConsumer {

    private final GraphBuildService graphBuildService;

    @RabbitListener(queues = "#{@kagGraphBuildQueue.name}", ackMode = "MANUAL")
    public void handleBuild(@Payload KAGReindexMessage message, Channel channel,
                            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            log.info("KAG build task received: taskId={}, type={}", message.getTaskId(), message.getType());

            switch (message.getType()) {
                case DELETE_BY_DOC_IDS -> {
                    if (message.getDocumentIds() != null) {
                        for (Long docId : message.getDocumentIds()) {
                            graphBuildService.deleteForDocument(docId);
                            log.info("KAG graph deleted for documentId={}", docId);
                        }
                    }
                }
                case BUILD_BY_DOC_IDS -> {
                    if (message.getDocumentIds() != null) {
                        graphBuildService.buildBatch(message.getDocumentIds());
                    }
                }
                case BUILD_ALL -> graphBuildService.buildAll();
            }

            channel.basicAck(deliveryTag, false);
            log.info("KAG build task completed: taskId={}", message.getTaskId());
        } catch (Exception e) {
            log.error("KAG build task failed: taskId={}, error={}", message.getTaskId(), e.getMessage(), e);
            try {
                channel.basicNack(deliveryTag, false, false);
            } catch (Exception ex) {
                log.error("Failed to nack KAG message", ex);
            }
        }
    }
}
