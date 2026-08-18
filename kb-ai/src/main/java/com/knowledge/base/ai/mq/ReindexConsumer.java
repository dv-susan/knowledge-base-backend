package com.knowledge.base.ai.mq;

import com.alibaba.fastjson2.JSON;
import com.knowledge.base.ai.client.DocumentFeignClient;
import com.knowledge.base.ai.config.RagProperties;
import com.knowledge.base.ai.rag.entity.DocumentChunk;
import com.knowledge.base.ai.dto.kag.DocumentRecordDTO;
import com.knowledge.base.ai.rag.service.ChunkingService;
import com.knowledge.base.ai.rag.service.EmbeddingService;
import com.knowledge.base.ai.rag.service.VectorIndexService;
import com.knowledge.base.ai.vo.ReindexProgressVO;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 重建索引消费者
 *
 * <p>从 RabbitMQ 消费索引任务，执行完整的文档→分块→嵌入→索引流水线。
 * 支持全量重建（ALL）和指定文档重建（BY_DOC_IDS）。
 * 使用手动ACK确保消息可靠性。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReindexConsumer {

    private final DocumentFeignClient documentFeignClient;
    private final ChunkingService chunkingService;
    private final EmbeddingService embeddingService;
    private final VectorIndexService vectorIndexService;
    private final RagProperties ragProperties;
    private final StringRedisTemplate redisTemplate;

    private static final String PROGRESS_KEY_PREFIX = "rag:reindex:progress:";
    private static final int PAGE_SIZE = 50;

    @RabbitListener(queues = "#{@ragReindexQueue.name}", ackMode = "MANUAL")
    public void handleReindex(ReindexMessage message, Channel channel,
                              @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            log.info("收到重建索引任务：taskId={}, type={}", message.getTaskId(), message.getType());

            updateProgress(message.getTaskId(), "RUNNING", 0, 0, 0);

            // DELETE_BY_DOC_IDS：仅删除向量索引，不重新索引
            if (message.getType() == ReindexMessage.ReindexType.DELETE_BY_DOC_IDS
                    && message.getDocumentIds() != null) {
                int deleted = 0;
                for (Long docId : message.getDocumentIds()) {
                    try {
                        vectorIndexService.deleteByDocId(docId);
                        deleted++;
                        log.info("已删除文档向量索引：documentId={}", docId);
                    } catch (Exception e) {
                        log.warn("删除文档向量索引失败：documentId={}, error={}", docId, e.getMessage());
                    }
                }
                updateProgressFinal(message.getTaskId(), "COMPLETED", message.getDocumentIds().size(), deleted, 0);
                log.info("删除向量索引完成：taskId={}, deleted={}", message.getTaskId(), deleted);
                channel.basicAck(deliveryTag, false);
                return;
            }

            List<DocumentRecordDTO> documents;
            if (message.getType() == ReindexMessage.ReindexType.BY_DOC_IDS && message.getDocumentIds() != null) {
                documents = loadDocumentsByIds(message.getDocumentIds());
            } else {
                documents = loadAllPublishedDocuments();
            }

            int total = documents.size();
            updateProgress(message.getTaskId(), "RUNNING", total, 0, 0);
            log.info("开始索引文档：taskId={}, totalDocuments={}", message.getTaskId(), total);

            int completed = 0;
            int failed = 0;
            List<Long> failedDocIds = new ArrayList<>();

            for (int i = 0; i < documents.size(); i += ragProperties.getAsync().getReindexBatchSize()) {
                int end = Math.min(i + ragProperties.getAsync().getReindexBatchSize(), documents.size());
                List<DocumentRecordDTO> batch = documents.subList(i, end);

                for (DocumentRecordDTO doc : batch) {
                    try {
                        Long docId = doc.getId();
                        String title = doc.getTitle() != null ? doc.getTitle() : "未命名文档";
                        String content = doc.getContent();
                        Long categoryId = doc.getCategoryId();
                        Long authorId = doc.getAuthorId();
                        Long teamId = doc.getTeamId();
                        Integer status = doc.getStatus();

                        if (content == null || content.isEmpty()) {
                            log.warn("文档内容为空，跳过：documentId={}", docId);
                            completed++;
                            continue;
                        }

                        // 删除旧块
                        vectorIndexService.deleteByDocId(docId);

                        // 分块 + 嵌入 + 索引
                        List<DocumentChunk> chunks = chunkingService.chunk(
                                content, docId, title, categoryId, authorId, teamId, status,
                                doc.getPublishTime());

                        if (!chunks.isEmpty()) {
                            List<String> texts = chunks.stream().map(DocumentChunk::getContent).toList();
                            List<float[]> embeddings = embeddingService.embedBatch(texts);
                            for (int j = 0; j < chunks.size(); j++) {
                                chunks.get(j).setEmbedding(embeddings.get(j));
                                chunks.get(j).setIndexedAt(LocalDateTime.now());
                            }
                            vectorIndexService.indexChunks(chunks);
                        }

                        completed++;
                    } catch (Exception e) {
                        log.error("文档索引失败：{}", e.getMessage(), e);
                        failed++;
                        failedDocIds.add(doc.getId());
                    }
                }

                updateProgress(message.getTaskId(), "RUNNING", total, completed, failed);
                log.debug("重建索引进度：taskId={}, completed={}/{}, failed={}",
                        message.getTaskId(), completed, total, failed);
            }

            // 完成
            updateProgressFinal(message.getTaskId(), "COMPLETED", total, completed, failed);
            log.info("重建索引完成：taskId={}, completed={}, failed={}", message.getTaskId(), completed, failed);
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("重建索引任务失败：taskId={}, error={}", message.getTaskId(), e.getMessage(), e);
            try {
                updateProgress(message.getTaskId(), "FAILED", 0, 0, 0);
                channel.basicNack(deliveryTag, false, false);
            } catch (Exception ex) {
                log.error("消息确认失败", ex);
            }
        }
    }

    private List<DocumentRecordDTO> loadAllPublishedDocuments() {
        List<DocumentRecordDTO> allDocs = new ArrayList<>();
        long current = 1;
        while (true) {
            Map<String, Object> page = documentFeignClient.pageDocuments(current, (long) PAGE_SIZE, 1);
            if (page == null) break;

            Object dataObj = page.get("data");
            if (!(dataObj instanceof Map)) break;

            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) dataObj;
            Object recordsObj = data.get("records");
            if (!(recordsObj instanceof List)) break;

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> records = (List<Map<String, Object>>) recordsObj;
            if (records.isEmpty()) break;

            // 对每条记录需要单独获取内容（因为分页接口不返回content）
            for (Map<String, Object> record : records) {
                Long docId = toLong(record.get("id"));
                if (docId != null) {
                    try {
                        DocumentRecordDTO detail = loadDocumentDetail(docId);
                        if (detail != null) {
                            allDocs.add(detail);
                        }
                    } catch (Exception e) {
                        log.warn("获取文档详情失败：documentId={}, error={}", docId, e.getMessage());
                    }
                }
            }

            if (records.size() < PAGE_SIZE) break;
            current++;
        }
        return allDocs;
    }

    private List<DocumentRecordDTO> loadDocumentsByIds(List<Long> docIds) {
        List<DocumentRecordDTO> docs = new ArrayList<>();
        for (Long docId : docIds) {
            try {
                DocumentRecordDTO detail = loadDocumentDetail(docId);
                if (detail != null) {
                    docs.add(detail);
                }
            } catch (Exception e) {
                log.warn("获取文档详情失败：documentId={}, error={}", docId, e.getMessage());
            }
        }
        return docs;
    }

    @SuppressWarnings("unchecked")
    private DocumentRecordDTO loadDocumentDetail(Long docId) {
        Map<String, Object> response = documentFeignClient.getDocument(docId);
        if (response == null) return null;
        Object dataObj = response.get("data");
        if (!(dataObj instanceof Map)) return null;
        return toDocumentRecord((Map<String, Object>) dataObj);
    }

    /**
     * 将Feign返回的Map转为DocumentRecordDTO
     */
    private DocumentRecordDTO toDocumentRecord(Map<String, Object> raw) {
        if (raw == null) return null;
        return DocumentRecordDTO.builder()
                .id(toLong(raw.get("id")))
                .title((String) raw.getOrDefault("title", "未命名文档"))
                .content((String) raw.get("content"))
                .categoryId(toLong(raw.get("categoryId")))
                .authorId(toLong(raw.get("authorId")))
                .authorName((String) raw.getOrDefault("authorName", ""))
                .teamId(toLong(raw.get("teamId")))
                .status(toInt(raw.get("status")))
                .summary((String) raw.get("summary"))
                .publishTime(getString(raw, "publishTime"))
                .build();
    }

    private void updateProgress(String taskId, String status, int total, int completed, int failed) {
        ReindexProgressVO progress = ReindexProgressVO.builder()
                .taskId(taskId)
                .status(status)
                .totalDocuments(total)
                .completedDocuments(completed)
                .failedDocuments(failed)
                .startTime(LocalDateTime.now())
                .build();
        try {
            redisTemplate.opsForValue().set(PROGRESS_KEY_PREFIX + taskId,
                    com.alibaba.fastjson2.JSON.toJSONString(progress), 1, TimeUnit.HOURS);
        } catch (Exception e) {
            log.warn("更新索引进度失败：{}", e.getMessage());
        }
    }

    private void updateProgressFinal(String taskId, String status, int total, int completed, int failed) {
        ReindexProgressVO progress = ReindexProgressVO.builder()
                .taskId(taskId)
                .status(status)
                .totalDocuments(total)
                .completedDocuments(completed)
                .failedDocuments(failed)
                .endTime(LocalDateTime.now())
                .build();
        try {
            redisTemplate.opsForValue().set(PROGRESS_KEY_PREFIX + taskId,
                    com.alibaba.fastjson2.JSON.toJSONString(progress), 1, TimeUnit.HOURS);
        } catch (Exception e) {
            log.warn("更新索引进度失败：{}", e.getMessage());
        }
    }

    private Long toLong(Object value) {
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).longValue();
        return Long.parseLong(value.toString());
    }

    private Integer toInt(Object value) {
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).intValue();
        return Integer.parseInt(value.toString());
    }

    private String getString(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? val.toString() : null;
    }
}
