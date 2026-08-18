package com.knowledge.base.ai.rag.kag.retrieval.impl;

import com.knowledge.base.ai.config.ModelProvider;
import com.knowledge.base.ai.config.KAGProperties;
import com.knowledge.base.ai.rag.kag.retrieval.GraphContext;
import com.knowledge.base.ai.rag.kag.retrieval.KAGRetrievalService;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * KAG 图谱检索服务实现
 *
 * <p>使用 LLM 从用户查询中提取实体关键词，然后在 Neo4j 中执行多跳遍历，
 * 反向查找关联的文档文本块，返回结构化的图谱上下文。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KAGRetrievalServiceImpl implements KAGRetrievalService {

    private final Neo4jClient neo4jClient;
    private final ModelProvider modelProvider;
    private final KAGProperties kagProperties;

    /** {@inheritDoc} */
    @Override
    public GraphContext retrieveGraphContext(String query) {
        int maxEntities = kagProperties.getRetrieval().getMaxEntitiesPerQuery();
        int maxHops = kagProperties.getRetrieval().getMaxHops();
        int maxChunks = kagProperties.getRetrieval().getMaxChunksPerEntity() * maxEntities;
        return retrieveGraphContext(query, maxEntities, maxHops, maxChunks);
    }

    /** {@inheritDoc} */
    @Override
    public GraphContext retrieveGraphContext(String query, int maxEntities, int maxHops, int maxChunks) {
        if (query == null || query.isBlank()) {
            return emptyContext();
        }

        int timeout = kagProperties.getRetrieval().getTimeoutSeconds();

        try {
            // Step 1: Extract entity keywords from the query using LLM
            List<String> keywords = extractEntityKeywords(query);
            log.debug("KAG query entities extracted: query='{}', keywords={}", query, keywords);

            if (keywords.isEmpty()) {
                return emptyContext();
            }

            // Step 2: Match entities in Neo4j
            List<GraphContext.GraphEntity> matchedEntities = matchEntities(keywords, maxEntities);
            if (matchedEntities.isEmpty()) {
                log.debug("No matching entities found in Neo4j for query: {}", query);
                return emptyContext();
            }

            // Step 3: Multi-hop traversal to discover related entities and paths
            List<GraphContext.GraphPath> paths = traverseGraph(matchedEntities, maxHops);

            // Step 4: Reverse lookup - find document chunks that mention these entities
            List<GraphContext.GraphChunk> chunks = findAssociatedChunks(matchedEntities, maxChunks);

            return GraphContext.builder()
                    .matchedEntities(matchedEntities)
                    .reasoningPaths(paths)
                    .associatedChunks(chunks)
                    .hasResults(true)
                    .build();

        } catch (Exception e) {
            log.warn("KAG graph retrieval failed: {} - falling back to empty context", e.getMessage());
            return emptyContext();
        }
    }

    // ==================== Private Methods ====================

    /**
     * 使用 LLM 从查询中提取实体关键词
     */
    private List<String> extractEntityKeywords(String query) {
        try {
            ChatLanguageModel model = modelProvider.getDefaultModel();
            String prompt = """
                    从以下用户查询中提取关键的知识实体名称（技术栈、API、配置项、核心概念、工具名等）。
                    只返回实体名称列表，每行一个，不要包含序号或其他内容。
                    最多提取5个最关键的实体。
                    如果查询中没有明确的技术实体，返回空。

                    用户查询：%s
                    """.formatted(query);

            String response = model.generate(
                    SystemMessage.from("你是一个实体识别助手。只返回实体名称，每行一个。"),
                    UserMessage.from(prompt)
            ).content().text();

            if (response == null || response.isBlank()) return Collections.emptyList();

            return Arrays.stream(response.split("\n"))
                    .map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.startsWith("-") && !line.matches("^\\d+[\\.\\)]"))
                    .limit(5)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.warn("LLM entity keyword extraction failed: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 在 Neo4j 中匹配实体（支持模糊搜索和别名）
     */
    private List<GraphContext.GraphEntity> matchEntities(List<String> keywords, int maxEntities) {
        List<GraphContext.GraphEntity> allMatched = new ArrayList<>();

        for (String keyword : keywords) {
            try {
                List<GraphContext.GraphEntity> matched = neo4jClient.query("""
                        MATCH (e:KnowledgeEntity)
                        WHERE e.name CONTAINS $keyword
                           OR any(a IN coalesce(e.aliases, []) WHERE a CONTAINS $keyword)
                        OPTIONAL MATCH (e)-[r:RELATED_TO|DEPENDS_ON|USES|CONFIGURES|HAS_PART]-(other:KnowledgeEntity)
                        RETURN DISTINCT e.name AS name, e.type AS type, e.description AS description,
                               count(DISTINCT other) AS connectionCount
                        ORDER BY connectionCount DESC, e.updatedAt DESC
                        LIMIT $limit
                        """)
                        .bind(keyword).to("keyword")
                        .bind(maxEntities).to("limit")
                        .fetch()
                        .all()
                        .stream()
                        .map(r -> GraphContext.GraphEntity.builder()
                                .name((String) r.get("name"))
                                .type((String) r.get("type"))
                                .description((String) r.get("description"))
                                .connectionCount(r.get("connectionCount") != null
                                        ? ((Number) r.get("connectionCount")).intValue() : 0)
                                .build())
                        .toList();

                allMatched.addAll(matched);
            } catch (Exception e) {
                log.warn("Neo4j entity matching failed for keyword '{}': {}", keyword, e.getMessage());
            }
        }

        // Dedup by name
        return allMatched.stream()
                .collect(Collectors.toMap(
                        GraphContext.GraphEntity::getName,
                        e -> e,
                        (e1, e2) -> e1))  // keep first
                .values().stream()
                .limit(maxEntities)
                .collect(Collectors.toList());
    }

    /**
     * 在 Neo4j 中执行多跳遍历
     */
    private List<GraphContext.GraphPath> traverseGraph(List<GraphContext.GraphEntity> entities, int maxHops) {
        List<GraphContext.GraphPath> allPaths = new ArrayList<>();

        for (GraphContext.GraphEntity entity : entities) {
            try {
                List<GraphContext.GraphPath> paths = neo4jClient.query("""
                        MATCH (start:KnowledgeEntity {name: $entityName})
                        MATCH path = (start)-[:DEPENDS_ON|USES|CONFIGURES|HAS_PART|RELATED_TO*1..%d]-
                                      (related:KnowledgeEntity)
                        WITH path, related, length(path) AS hops
                        WHERE hops <= $maxHops
                        RETURN [n IN nodes(path) | n.name] AS nodeNames,
                               [r IN relationships(path) | coalesce(r.relation, type(r))] AS relationTypes,
                               hops
                        ORDER BY hops ASC
                        LIMIT 5
                        """.formatted(maxHops))
                        .bind(entity.getName()).to("entityName")
                        .bind(maxHops).to("maxHops")
                        .fetch()
                        .all()
                        .stream()
                        .map(r -> {
                            @SuppressWarnings("unchecked")
                            List<String> nodes = (List<String>) r.get("nodeNames");
                            @SuppressWarnings("unchecked")
                            List<String> rels = (List<String>) r.get("relationTypes");
                            return GraphContext.GraphPath.builder()
                                    .nodes(nodes != null ? nodes : Collections.emptyList())
                                    .relations(rels != null ? rels : Collections.emptyList())
                                    .hops(r.get("hops") != null ? ((Number) r.get("hops")).intValue() : 0)
                                    .build();
                        })
                        .toList();

                allPaths.addAll(paths);
            } catch (Exception e) {
                log.warn("Neo4j traversal failed for entity '{}': {}", entity.getName(), e.getMessage());
            }
        }

        // Dedup paths and limit
        return allPaths.stream()
                .filter(p -> p.getNodes() != null && p.getNodes().size() >= 2)
                .distinct()
                .limit(10)
                .collect(Collectors.toList());
    }

    /**
     * 反向查找：从实体找到关联的文档文本块
     */
    private List<GraphContext.GraphChunk> findAssociatedChunks(
            List<GraphContext.GraphEntity> entities, int maxChunks) {

        List<GraphContext.GraphChunk> allChunks = new ArrayList<>();

        for (GraphContext.GraphEntity entity : entities) {
            try {
                List<GraphContext.GraphChunk> chunks = neo4jClient.query("""
                        MATCH (e:KnowledgeEntity {name: $entityName})<-[:MENTIONS]-(c:DocumentChunk)
                        OPTIONAL MATCH (c)-[:HAS_CHUNK]->(d:KnowledgeDocument)
                        WHERE d.status = 1
                        RETURN c.chunkId AS chunkId, c.content AS content, c.heading AS heading,
                               coalesce(d.docId, c.docId) AS docId,
                               coalesce(d.title, '未知文档') AS docTitle,
                               e.name AS entityName
                        ORDER BY c.chunkIndex ASC
                        LIMIT $limit
                        """)
                        .bind(entity.getName()).to("entityName")
                        .bind(kagProperties.getRetrieval().getMaxChunksPerEntity()).to("limit")
                        .fetch()
                        .all()
                        .stream()
                        .map(r -> GraphContext.GraphChunk.builder()
                                .chunkId((String) r.get("chunkId"))
                                .content((String) r.get("content"))
                                .heading((String) r.get("heading"))
                                .docId(r.get("docId") != null ? ((Number) r.get("docId")).longValue() : null)
                                .docTitle((String) r.get("docTitle"))
                                .entityName((String) r.get("entityName"))
                                .sourcePath(entity.getName())
                                .build())
                        .toList();

                allChunks.addAll(chunks);
            } catch (Exception e) {
                log.warn("Failed to find associated chunks for entity '{}': {}",
                        entity.getName(), e.getMessage());
            }
        }

        // Dedup by chunkId
        return allChunks.stream()
                .collect(Collectors.toMap(
                        GraphContext.GraphChunk::getChunkId,
                        c -> c,
                        (c1, c2) -> c1))
                .values().stream()
                .limit(maxChunks)
                .collect(Collectors.toList());
    }

    private GraphContext emptyContext() {
        return GraphContext.builder()
                .matchedEntities(Collections.emptyList())
                .reasoningPaths(Collections.emptyList())
                .associatedChunks(Collections.emptyList())
                .hasResults(false)
                .build();
    }
}
