package com.knowledge.base.ai.rag.service.impl;

import com.alibaba.fastjson2.JSON;
import com.knowledge.base.ai.mq.ReindexMessage;
import com.knowledge.base.ai.rag.service.ReindexService;
import com.knowledge.base.ai.vo.ReindexProgressVO;
import com.knowledge.base.common.config.InstanceIdentifier;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 重建索引服务实现
 *
 * <p>发送索引任务到 RabbitMQ，通过 Redis 跟踪进度。
 * 路由键通过 InstanceIdentifier 进行实例隔离，
 * 确保多开发者本地环境的消息互不干扰。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReindexServiceImpl implements ReindexService {

    private final RabbitTemplate rabbitTemplate;
    private final StringRedisTemplate redisTemplate;

    @Resource
    private InstanceIdentifier instanceIdentifier;

    private static final String EXCHANGE = "rag.reindex.exchange";
    private static final String PROGRESS_KEY_PREFIX = "rag:reindex:progress:";

    private String routingKeyAll() {
        return "rag.reindex." + instanceIdentifier.getId() + ".all";
    }

    private String routingKeyByIds() {
        return "rag.reindex." + instanceIdentifier.getId() + ".by_ids";
    }

    private String routingKeyDelete() {
        return "rag.reindex." + instanceIdentifier.getId() + ".delete";
    }

    /** {@inheritDoc} */
    @Override
    public String reindexAll() {
        String taskId = UUID.randomUUID().toString();
        ReindexMessage message = ReindexMessage.builder()
                .taskId(taskId)
                .type(ReindexMessage.ReindexType.ALL)
                .build();
        rabbitTemplate.convertAndSend(EXCHANGE, routingKeyAll(), message);
        log.info("已发送全量重建索引任务：taskId={}", taskId);
        return taskId;
    }

    /** {@inheritDoc} */
    @Override
    public String reindexByDocId(Long documentId) {
        return reindexBatch(List.of(documentId));
    }

    /** {@inheritDoc} */
    @Override
    public String reindexBatch(List<Long> documentIds) {
        String taskId = UUID.randomUUID().toString();
        ReindexMessage message = ReindexMessage.builder()
                .taskId(taskId)
                .type(ReindexMessage.ReindexType.BY_DOC_IDS)
                .documentIds(documentIds)
                .build();
        rabbitTemplate.convertAndSend(EXCHANGE, routingKeyByIds(), message);
        log.info("已发送批量重建索引任务：taskId={}, docCount={}", taskId, documentIds.size());
        return taskId;
    }

    /** {@inheritDoc} */
    @Override
    public String deleteByDocId(Long documentId) {
        return deleteByDocIds(List.of(documentId));
    }

    /** {@inheritDoc} */
    @Override
    public String deleteByDocIds(List<Long> documentIds) {
        String taskId = UUID.randomUUID().toString();
        ReindexMessage message = ReindexMessage.builder()
                .taskId(taskId)
                .type(ReindexMessage.ReindexType.DELETE_BY_DOC_IDS)
                .documentIds(documentIds)
                .build();
        rabbitTemplate.convertAndSend(EXCHANGE, routingKeyDelete(), message);
        log.info("已发送删除向量索引任务：taskId={}, docCount={}", taskId, documentIds.size());
        return taskId;
    }

    /** {@inheritDoc} */
    @Override
    public ReindexProgressVO getProgress(String taskId) {
        try {
            String json = redisTemplate.opsForValue().get(PROGRESS_KEY_PREFIX + taskId);
            if (json == null) {
                return ReindexProgressVO.builder()
                        .taskId(taskId)
                        .status("NOT_FOUND")
                        .build();
            }
            return JSON.parseObject(json, ReindexProgressVO.class);
        } catch (Exception e) {
            log.warn("获取索引进度失败：taskId={}, error={}", taskId, e.getMessage());
            return ReindexProgressVO.builder().taskId(taskId).status("ERROR").build();
        }
    }
}
