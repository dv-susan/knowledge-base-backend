package com.knowledge.base.ai.rag.kag.graph.impl;

import com.knowledge.base.ai.client.DocumentFeignClient;
import com.knowledge.base.ai.client.GraphFeignClient;
import com.knowledge.base.ai.rag.entity.DocumentChunk;
import com.knowledge.base.ai.config.KAGProperties;
import com.knowledge.base.ai.dto.kag.DocumentRecordDTO;
import com.knowledge.base.ai.rag.kag.extraction.EntityNormalizer;
import com.knowledge.base.ai.rag.kag.extraction.ExtractionService;
import com.knowledge.base.ai.dto.kag.extraction.ExtractedEntity;
import com.knowledge.base.ai.dto.kag.extraction.ExtractedRelation;
import com.knowledge.base.ai.dto.kag.extraction.ExtractionResult;
import com.knowledge.base.ai.rag.kag.graph.GraphBuildService;
import com.knowledge.base.ai.rag.service.ChunkingService;
import com.knowledge.base.ai.mq.KAGReindexMessage;
import com.knowledge.base.ai.config.KAGRabbitConfig;
import com.knowledge.base.common.config.InstanceIdentifier;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 图谱构建服务实现
 *
 * <p>编排完整的 KAG 图谱构建流水线：
 * 1. 通过 Feign 从 kb-document 拉取文档内容
 * 2. 使用 ChunkingService 对文档内容分块
 * 3. 逐块调用 LLM 抽取实体和关系
 * 4. 使用 EntityNormalizer 去重合并实体
 * 5. 批量写入 Neo4j（Document → Chunk → Entity 节点 + 关系）</p>
 *
 * <p>路由键通过 InstanceIdentifier 进行实例隔离，
 * 确保多开发者本地环境的消息互不干扰。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GraphBuildServiceImpl implements GraphBuildService {

    private final DocumentFeignClient documentFeignClient;
    private final GraphFeignClient graphFeignClient;
    private final ChunkingService chunkingService;
    private final ExtractionService extractionService;
    private final EntityNormalizer entityNormalizer;
    private final KAGProperties kagProperties;
    private final Neo4jClient neo4jClient;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private InstanceIdentifier instanceIdentifier;

    private static final int PAGE_SIZE = 20;

    /** {@inheritDoc} */
    @Override
    public int buildForDocument(Long docId) {
        log.info("KAG graph build started for documentId={}", docId);

        DocumentRecordDTO doc = fetchDocumentDetail(docId);
        if (doc == null) {
            log.warn("Document not found: documentId={}", docId);
            return 0;
        }

        return buildGraphForDocument(doc);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteForDocument(Long docId) {
        log.info("KAG graph delete for documentId={}", docId);
        try {
            // Delete document node and all cascaded chunks + relationships
            neo4jClient.query("""
                    MATCH (d:KnowledgeDocument {docId: $docId})
                    OPTIONAL MATCH (d)-[:HAS_CHUNK]->(c:DocumentChunk)
                    DETACH DELETE d, c
                    """)
                    .bind(docId).to("docId")
                    .run();

            // Clean up orphaned KnowledgeEntity nodes that no longer have MENTIONS from any chunk
            neo4jClient.query("""
                    MATCH (e:KnowledgeEntity)
                    WHERE NOT EXISTS { MATCH (e)<-[:MENTIONS]-() }
                    DETACH DELETE e
                    """)
                    .run();

            log.info("KAG graph deleted for documentId={}", docId);
        } catch (Exception e) {
            log.warn("Failed to delete KAG graph for documentId={}: {}", docId, e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public int buildAll() {
        log.info("KAG graph build ALL started");
        int processed = 0;

        long current = 1;
        while (true) {
            Map<String, Object> page = documentFeignClient.pageDocuments(current, (long) PAGE_SIZE, 1);
            if (page == null) break;

            Map<String, Object> data = (Map<String, Object>) page.get("data");
            if (data == null) break;

            List<Map<String, Object>> recordsRaw = (List<Map<String, Object>>) data.get("records");
            if (recordsRaw == null || recordsRaw.isEmpty()) break;

            List<DocumentRecordDTO> records = recordsRaw.stream()
                    .map(this::toDocumentRecord)
                    .filter(Objects::nonNull)
                    .toList();

            for (DocumentRecordDTO record : records) {
                try {
                    DocumentRecordDTO doc = fetchDocumentDetail(record.getId());
                    if (doc != null) {
                        int entities = buildGraphForDocument(doc);
                        processed++;
                        log.info("KAG graph built for documentId={}, entities={}", record.getId(), entities);
                    }
                } catch (Exception e) {
                    log.warn("KAG graph build failed for documentId={}: {}", record.getId(), e.getMessage());
                }
            }

            if (records.size() < PAGE_SIZE) break;
            current++;
        }

        log.info("KAG graph build ALL completed: {} documents processed", processed);
        return processed;
    }

    /** {@inheritDoc} */
    @Override
    public int buildBatch(List<Long> docIds) {
        if (docIds == null || docIds.isEmpty()) return 0;

        log.info("KAG graph build batch started: {} documents", docIds.size());
        int processed = 0;

        for (Long docId : docIds) {
            try {
                int entities = buildForDocument(docId);
                if (entities > 0) processed++;
            } catch (Exception e) {
                log.warn("KAG graph build failed for documentId={}: {}", docId, e.getMessage());
            }
        }

        log.info("KAG graph build batch completed: {} documents processed", processed);
        return processed;
    }

    // ==================== 异步任务发布 ====================

    /** {@inheritDoc} */
    @Override
    public String publishBuildTask(Long docId) {
        String taskId = UUID.randomUUID().toString();
        try {
            KAGReindexMessage message = KAGReindexMessage.builder()
                    .taskId(taskId)
                    .type(KAGReindexMessage.KAGBuildType.BUILD_BY_DOC_IDS)
                    .documentIds(List.of(docId))
                    .build();
            rabbitTemplate.convertAndSend(
                    KAGRabbitConfig.EXCHANGE,
                    routingKeyBuildByIds(),
                    message);
            log.info("KAG build task published: taskId={}, docId={}", taskId, docId);
        } catch (Exception e) {
            log.warn("Failed to publish KAG build, running synchronously: docId={}", docId);
            buildForDocument(docId);
        }
        return taskId;
    }

    /** {@inheritDoc} */
    @Override
    public String publishBuildBatchTask(List<Long> docIds) {
        String taskId = UUID.randomUUID().toString();
        try {
            KAGReindexMessage message = KAGReindexMessage.builder()
                    .taskId(taskId)
                    .type(KAGReindexMessage.KAGBuildType.BUILD_BY_DOC_IDS)
                    .documentIds(docIds)
                    .build();
            rabbitTemplate.convertAndSend(
                    KAGRabbitConfig.EXCHANGE,
                    routingKeyBuildByIds(),
                    message);
            log.info("KAG batch build task published: taskId={}, count={}", taskId, docIds.size());
        } catch (Exception e) {
            log.warn("Failed to publish KAG batch build, running synchronously");
            buildBatch(docIds);
        }
        return taskId;
    }

    /** {@inheritDoc} */
    @Override
    public String publishBuildAllTask() {
        String taskId = UUID.randomUUID().toString();
        try {
            KAGReindexMessage message = KAGReindexMessage.builder()
                    .taskId(taskId)
                    .type(KAGReindexMessage.KAGBuildType.BUILD_ALL)
                    .build();
            rabbitTemplate.convertAndSend(
                    KAGRabbitConfig.EXCHANGE,
                    routingKeyBuildAll(),
                    message);
            log.info("KAG build all task published: taskId={}", taskId);
        } catch (Exception e) {
            log.warn("Failed to publish KAG build all, running synchronously");
            buildAll();
        }
        return taskId;
    }

    /** {@inheritDoc} */
    @Override
    public String publishDeleteTask(Long docId) {
        String taskId = UUID.randomUUID().toString();
        try {
            KAGReindexMessage message = KAGReindexMessage.builder()
                    .taskId(taskId)
                    .type(KAGReindexMessage.KAGBuildType.DELETE_BY_DOC_IDS)
                    .documentIds(List.of(docId))
                    .build();
            rabbitTemplate.convertAndSend(
                    KAGRabbitConfig.EXCHANGE,
                    routingKeyDelete(),
                    message);
            log.info("KAG delete task published: taskId={}, docId={}", taskId, docId);
        } catch (Exception e) {
            log.warn("Failed to publish KAG delete, running synchronously");
            deleteForDocument(docId);
        }
        return taskId;
    }

    // ==================== Private Methods ====================

    /**
     * 为单个文档构建图谱
     *
     * @param doc 从Feign获取的文档数据
     * @return 构建的实体数
     */
    private int buildGraphForDocument(DocumentRecordDTO doc) {
        Long docId = doc.getId();
        String title = doc.getTitle() != null ? doc.getTitle() : "未命名文档";
        String content = doc.getContent();
        Long categoryId = doc.getCategoryId();
        Long authorId = doc.getAuthorId();
        String authorName = doc.getAuthorName() != null ? doc.getAuthorName() : "";
        Integer status = doc.getStatus();

        if (content == null || content.isBlank()) {
            log.info("Document content is empty, skipping KAG build: documentId={}", docId);
            return 0;
        }

        // Step 0: Clear existing graph data for this document if configured
        if (kagProperties.getGraph().isClearBeforeBuild()) {
            deleteForDocument(docId);
        }

        // Step 1: Create Document node
        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        createDocumentNode(docId, title, getSummary(doc), categoryId, authorId, authorName, status, now);

        // Step 2: Chunk the content
        List<DocumentChunk> chunks = chunkingService.chunk(
                content, docId, title, categoryId, authorId, null, status, doc.getPublishTime());
        log.debug("Document chunked: documentId={}, totalChunks={}", docId, chunks.size());

        // Step 3 & 4: Extract entities/relations and normalize
        int totalEntities = 0;
        for (DocumentChunk chunk : chunks) {
            try {
                // Create Chunk node
                String chunkId = chunk.getChunkId();
                createChunkNode(chunkId, docId, chunk.getContent(), chunk.getHeading(),
                        chunk.getChunkIndex(), chunk.getTotalChunks(), now);

                // Create HAS_CHUNK relationship
                createHasChunkRelation(docId, chunkId, chunk.getChunkIndex());

                // Extract entities/relations
                ExtractionResult result = extractionService.extract(
                        chunk.getContent(), chunk.getHeading(), docId, title);
                if (result == null || result.isEmpty()) continue;

                result.setChunkId(chunkId);

                // Normalize entities
                List<ExtractedEntity> normalized = entityNormalizer.normalize(result.getEntities());
                result.setEntities(normalized);

                // Write entities and relations to Neo4j
                totalEntities += writeToNeo4j(result);
            } catch (Exception e) {
                log.warn("Failed to process chunk for KAG: chunkId={}, docId={}, error={}",
                        chunk.getChunkId(), docId, e.getMessage());
            }
        }

        log.info("KAG graph built: documentId={}, chunks={}, entities={}",
                docId, chunks.size(), totalEntities);
        log.info("知识图谱构建完成：documentId={}, 实体数={}", docId, totalEntities);

        // Invalidate kb-graph's Redis cache so the frontend sees fresh data immediately
        try {
            graphFeignClient.evictAllGraphCaches();
            log.info("KAG graph cache evicted for documentId={}", docId);
        } catch (Exception e) {
            log.warn("Failed to evict kb-graph cache for documentId={}: {}", docId, e.getMessage());
        }

        return totalEntities;
    }

    /**
     * 写抽取结果到Neo4j
     */
    private int writeToNeo4j(ExtractionResult result) {
        int entityCount = 0;
        int relationCount = 0;
        int mentionsCount = 0;
        int entityFailures = 0;
        int relationFailures = 0;
        int mentionsFailures = 0;
        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        int totalEntities = result.getEntities() != null ? result.getEntities().size() : 0;
        int totalRelations = result.getRelations() != null ? result.getRelations().size() : 0;

        log.info("[KAG写入] 开始写入Neo4j: chunkId={}, entities={}, relations={}",
                result.getChunkId(), totalEntities, totalRelations);

        // Write entities
        if (result.getEntities() != null && !result.getEntities().isEmpty()) {
            log.info("[KAG写入] 开始写入 {} 个实体到Neo4j", totalEntities);
            for (int i = 0; i < result.getEntities().size(); i++) {
                ExtractedEntity entity = result.getEntities().get(i);
                try {
                    List<String> aliases = entity.getAliases() != null
                            ? entity.getAliases() : Collections.emptyList();
                    log.debug("[KAG写入] 写入实体 {}/{}: name={}, type={}, aliases={}",
                            i + 1, totalEntities, entity.getName(), entity.getType(), aliases);

                    neo4jClient.query("""
                            MERGE (e:KnowledgeEntity {name: $name})
                            ON CREATE SET e.type = $type, e.description = $description,
                                          e.aliases = $aliases, e.createdAt = $now, e.updatedAt = $now
                            ON MATCH SET e.type = coalesce($type, e.type),
                                         e.description = CASE WHEN $description <> '' THEN $description ELSE e.description END,
                                         e.aliases = CASE WHEN e.aliases IS NOT NULL THEN e.aliases + $aliases ELSE $aliases END,
                                         e.updatedAt = $now
                            """)
                            .bind(entity.getName()).to("name")
                            .bind(entity.getType()).to("type")
                            .bind(entity.getDescription() != null ? entity.getDescription() : "").to("description")
                            .bind(aliases).to("aliases")
                            .bind(now).to("now")
                            .run();
                    entityCount++;
                    log.debug("[KAG写入] 实体写入成功: {}", entity.getName());
                } catch (Exception e) {
                    entityFailures++;
                    log.error("[KAG写入] 实体写入失败 ({}/{}, name={}): {}",
                            i + 1, totalEntities, entity.getName(), e.getMessage(), e);
                }
            }
            log.info("[KAG写入] 实体写入完成: 成功={}, 失败={}", entityCount, entityFailures);
        } else {
            log.info("[KAG写入] 没有实体需要写入");
        }

        // Write relationships between entities
        if (result.getRelations() != null && !result.getRelations().isEmpty()) {
            log.info("[KAG写入] 开始写入 {} 个关系到Neo4j", totalRelations);
            for (int i = 0; i < result.getRelations().size(); i++) {
                ExtractedRelation rel = result.getRelations().get(i);
                try {
                    String safeRelType = rel.getRelation().replaceAll("[^A-Za-z0-9_]", "_");
                    log.debug("[KAG写入] 写入关系 {}/{}: {} -[{}]-> {}, weight={}",
                            i + 1, totalRelations, rel.getSource(), rel.getRelation(), rel.getTarget(), rel.getWeight());

                    neo4jClient.query("""
                            MATCH (a:KnowledgeEntity {name: $source})
                            MATCH (b:KnowledgeEntity {name: $target})
                            MERGE (a)-[r:RELATED_TO]->(b)
                            ON CREATE SET r.relation = $relType, r.weight = $weight, r.createdAt = datetime()
                            ON MATCH SET r.weight = coalesce($weight, r.weight)
                            """)
                            .bind(rel.getSource()).to("source")
                            .bind(rel.getTarget()).to("target")
                            .bind(rel.getRelation()).to("relType")
                            .bind(rel.getWeight()).to("weight")
                            .run();
                    relationCount++;
                    log.debug("[KAG写入] 关系写入成功: {} -> {}", rel.getSource(), rel.getTarget());
                } catch (Exception e) {
                    relationFailures++;
                    log.error("[KAG写入] 关系写入失败 ({}/{}, {}->{}): {}",
                            i + 1, totalRelations, rel.getSource(), rel.getTarget(), e.getMessage(), e);
                }
            }
            log.info("[KAG写入] 关系写入完成: 成功={}, 失败={}", relationCount, relationFailures);
        } else {
            log.info("[KAG写入] 没有关系需要写入");
        }

        // Create MENTIONS relationships (chunk -> entity)
        if (result.getChunkId() != null && result.getEntities() != null && !result.getEntities().isEmpty()) {
            log.info("[KAG写入] 开始创建 MENTIONS 关系: chunkId={}, entityCount={}",
                    result.getChunkId(), result.getEntities().size());
            for (ExtractedEntity entity : result.getEntities()) {
                try {
                    log.debug("[KAG写入] 创建 MENTIONS: chunk={} -> entity={}",
                            result.getChunkId(), entity.getName());

                    neo4jClient.query("""
                            MATCH (c:DocumentChunk {chunkId: $chunkId})
                            MATCH (e:KnowledgeEntity {name: $entityName})
                            MERGE (c)-[r:MENTIONS]->(e)
                            ON CREATE SET r.confidence = $confidence, r.chunkId = $chunkId
                            ON MATCH SET r.confidence = coalesce($confidence, r.confidence)
                            """)
                            .bind(result.getChunkId()).to("chunkId")
                            .bind(entity.getName()).to("entityName")
                            .bind(entity.getConfidence()).to("confidence")
                            .run();
                    mentionsCount++;
                } catch (Exception e) {
                    mentionsFailures++;
                    log.warn("[KAG写入] MENTIONS 关系创建失败: chunk={}, entity={}, error={}",
                            result.getChunkId(), entity.getName(), e.getMessage());
                }
            }
            log.info("[KAG写入] MENTIONS 关系创建完成: 成功={}, 失败={}", mentionsCount, mentionsFailures);
        }

        log.info("[KAG写入] 写入完成统计: 实体(成功/失败)={}/{}, 关系(成功/失败)={}/{}, MENTIONS(成功/失败)={}/{}",
                entityCount, entityFailures, relationCount, relationFailures, mentionsCount, mentionsFailures);

        return entityCount;
    }

    /**
     * 通过Feign获取文档详情
     */
    @SuppressWarnings("unchecked")
    private DocumentRecordDTO fetchDocumentDetail(Long docId) {
        try {
            Map<String, Object> response = documentFeignClient.getDocument(docId);
            if (response == null) return null;
            Object dataObj = response.get("data");
            if (!(dataObj instanceof Map)) return null;
            return toDocumentRecord((Map<String, Object>) dataObj);
        } catch (Exception e) {
            log.warn("Failed to fetch document detail via Feign: documentId={}, error={}",
                    docId, e.getMessage());
            return null;
        }
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
                .build();
    }

    private void createDocumentNode(Long docId, String title, String summary,
                                     Long categoryId, Long authorId, String authorName,
                                     Integer status, String now) {
        neo4jClient.query("""
                MERGE (d:KnowledgeDocument {docId: $docId})
                ON CREATE SET d.title = $title, d.summary = $summary, d.categoryId = $categoryId,
                              d.authorId = $authorId, d.authorName = $authorName, d.status = $status,
                              d.createdAt = $now, d.updatedAt = $now
                ON MATCH SET d.title = $title, d.summary = $summary, d.categoryId = $categoryId,
                             d.status = $status, d.updatedAt = $now
                """)
                .bind(docId).to("docId")
                .bind(title).to("title")
                .bind(summary != null ? summary : "").to("summary")
                .bind(categoryId).to("categoryId")
                .bind(authorId).to("authorId")
                .bind(authorName != null ? authorName : "").to("authorName")
                .bind(status != null ? status : 1).to("status")
                .bind(now).to("now")
                .run();
    }

    private void createChunkNode(String chunkId, Long docId, String content, String heading,
                                  int chunkIndex, int totalChunks, String now) {
        neo4jClient.query("""
                MERGE (c:DocumentChunk {chunkId: $chunkId})
                ON CREATE SET c.docId = $docId, c.content = $content, c.heading = $heading,
                              c.chunkIndex = $chunkIndex, c.totalChunks = $totalChunks, c.createdAt = $now
                ON MATCH SET c.docId = $docId, c.content = $content, c.heading = $heading,
                             c.chunkIndex = $chunkIndex, c.totalChunks = $totalChunks
                """)
                .bind(chunkId).to("chunkId")
                .bind(docId).to("docId")
                .bind(content != null ? content : "").to("content")
                .bind(heading != null ? heading : "").to("heading")
                .bind(chunkIndex).to("chunkIndex")
                .bind(totalChunks).to("totalChunks")
                .bind(now).to("now")
                .run();
    }

    private void createHasChunkRelation(Long docId, String chunkId, int chunkIndex) {
        neo4jClient.query("""
                MATCH (d:KnowledgeDocument {docId: $docId})
                MATCH (c:DocumentChunk {chunkId: $chunkId})
                MERGE (d)-[r:HAS_CHUNK]->(c)
                ON CREATE SET r.chunkIndex = $chunkIndex
                """)
                .bind(docId).to("docId")
                .bind(chunkId).to("chunkId")
                .bind(chunkIndex).to("chunkIndex")
                .run();
    }

    private String getSummary(DocumentRecordDTO doc) {
        String summary = doc.getSummary();
        if (summary != null && !summary.isBlank()) {
            return summary.length() > 200 ? summary.substring(0, 200) : summary;
        }
        return "";
    }

    // ==================== Routing Key Helpers (Instance-Scoped) ====================

    private String routingKeyBuildAll() {
        return "kag.graph.build." + instanceIdentifier.getId() + ".all";
    }

    private String routingKeyBuildByIds() {
        return "kag.graph.build." + instanceIdentifier.getId() + ".by_ids";
    }

    private String routingKeyDelete() {
        return "kag.graph.delete." + instanceIdentifier.getId();
    }

    private Long toLong(Object value) {
        if (value instanceof Number) return ((Number) value).longValue();
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private Integer toInt(Object value) {
        if (value instanceof Number) return ((Number) value).intValue();
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
