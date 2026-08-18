package com.knowledge.base.graph.repository;

import com.knowledge.base.graph.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 自定义图谱查询Repository实现
 *
 * <p>使用 Neo4jClient 执行复杂 Cypher 查询和批量写入操作。</p>
 *
 * <p>注意：{@code .fetch().all()} 返回 {@code Collection<Map<String, Object>>}，
 * {@code .fetchAs(Class).mappedBy(BiFunction)} 才提供原生 {@code org.neo4j.driver.Record}。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomGraphRepositoryImpl implements CustomGraphRepository {

    private final Neo4jClient neo4jClient;

    @Override
    @SuppressWarnings("unchecked")
    public List<GraphPathResultDTO> findShortestPaths(String source, String target, int maxDepth) {
        String cypher = """
                MATCH path = shortestPath((a:KnowledgeEntity {name: $source})-[*1..%d]-(b:KnowledgeEntity {name: $target}))
                RETURN [n IN nodes(path) | {name: n.name, type: n.type}] AS nodes,
                       [r IN relationships(path) | type(r)] AS relationships,
                       length(path) AS hops
                """.formatted(maxDepth);

        return neo4jClient.query(cypher)
                .bind(source).to("source")
                .bind(target).to("target")
                .fetch()
                .all()
                .stream()
                .map(r -> {
                    List<Map<String, Object>> nodesRaw = (List<Map<String, Object>>) r.get("nodes");
                    List<NodeInfo> nodes = new ArrayList<>();
                    if (nodesRaw != null) {
                        for (Map<String, Object> n : nodesRaw) {
                            nodes.add(NodeInfo.builder()
                                    .name((String) n.get("name"))
                                    .type((String) n.get("type"))
                                    .build());
                        }
                    }

                    List<String> relationships = (List<String>) r.get("relationships");
                    Long hops = toLong(r.get("hops"));

                    return GraphPathResultDTO.builder()
                            .nodes(nodes)
                            .relationships(relationships != null ? relationships : Collections.emptyList())
                            .hops(hops)
                            .build();
                })
                .toList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<TraverseResultDTO> traverseFromEntity(String entityName, int maxHops, int limit) {
        String cypher = """
                MATCH (start:KnowledgeEntity {name: $entityName})
                MATCH path = (start)-[:DEPENDS_ON|USES|CONFIGURES|HAS_PART|RELATED_TO*1..%d]-(related:KnowledgeEntity)
                WITH DISTINCT related, path, length(path) AS hops
                ORDER BY hops ASC
                LIMIT $limit
                RETURN related.name AS entityName, related.type AS entityType, related.description AS description,
                       [r IN relationships(path) | type(r)] AS pathRelations,
                       hops AS hops
                """.formatted(maxHops);

        return neo4jClient.query(cypher)
                .bind(entityName).to("entityName")
                .bind(limit).to("limit")
                .fetch()
                .all()
                .stream()
                .map(r -> {
                    List<String> pathRelations = (List<String>) r.get("pathRelations");
                    return TraverseResultDTO.builder()
                            .entityName((String) r.get("entityName"))
                            .entityType((String) r.get("entityType"))
                            .description((String) r.getOrDefault("description", ""))
                            .pathRelations(pathRelations != null ? pathRelations : Collections.emptyList())
                            .hops(toLong(r.get("hops")))
                            .build();
                })
                .toList();
    }

    @Override
    public GraphStatsDTO getGraphStatistics() {
        // 节点总数 - fetchAs + mappedBy 提供原生 Record
        Long nodeCount = neo4jClient.query("MATCH (n) RETURN count(n) AS count")
                .fetchAs(Long.class).mappedBy((t, r) -> r.get("count").asLong())
                .one().orElse(0L);

        // 关系总数
        Long relCount = neo4jClient.query("MATCH ()-[r]->() RETURN count(r) AS count")
                .fetchAs(Long.class).mappedBy((t, r) -> r.get("count").asLong())
                .one().orElse(0L);

        // 按类型统计实体数 - fetch().all() 返回 Map
        List<GraphStatsDTO.EntityTypeCountDTO> typeStats = neo4jClient.query(
                        "MATCH (e:KnowledgeEntity) RETURN e.type AS type, count(e) AS count ORDER BY count DESC")
                .fetch()
                .all()
                .stream()
                .map(r -> GraphStatsDTO.EntityTypeCountDTO.builder()
                        .type((String) r.getOrDefault("type", "Unknown"))
                        .count(toLong(r.get("count")))
                        .build())
                .toList();

        // 文档数
        Long docCount = neo4jClient.query("MATCH (d:KnowledgeDocument) WHERE d.status = 1 RETURN count(d) AS count")
                .fetchAs(Long.class).mappedBy((t, r) -> r.get("count").asLong())
                .one().orElse(0L);

        // 分块数
        Long chunkCount = neo4jClient.query("MATCH (c:DocumentChunk) RETURN count(c) AS count")
                .fetchAs(Long.class).mappedBy((t, r) -> r.get("count").asLong())
                .one().orElse(0L);

        return GraphStatsDTO.builder()
                .nodeCount(nodeCount)
                .edgeCount(relCount)
                .entityTypeStats(typeStats)
                .documentCount(docCount)
                .chunkCount(chunkCount)
                .build();
    }

    @Override
    public List<CommunityMemberDTO> detectCommunities(int minCommunitySize) {
        String cypher = """
                MATCH (e:KnowledgeEntity)-[r:DEPENDS_ON|USES|CONFIGURES|HAS_PART|RELATED_TO]-(other:KnowledgeEntity)
                WITH e, count(DISTINCT other) AS degree
                WHERE degree >= $minSize
                RETURN e.name AS name, e.type AS type, degree AS degree
                ORDER BY degree DESC
                LIMIT 100
                """;

        return neo4jClient.query(cypher)
                .bind(minCommunitySize).to("minSize")
                .fetch()
                .all()
                .stream()
                .map(r -> CommunityMemberDTO.builder()
                        .name((String) r.get("name"))
                        .type((String) r.getOrDefault("type", "Unknown"))
                        .degree(toLong(r.get("degree")))
                        .build())
                .toList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public SubgraphResultDTO searchSubgraph(String keyword, int maxNodes) {
        String nodeCypher = """
                MATCH (e:KnowledgeEntity)
                WHERE e.name CONTAINS $keyword OR any(a IN coalesce(e.aliases, []) WHERE a CONTAINS $keyword)
                WITH e LIMIT $maxNodes
                OPTIONAL MATCH (e)-[r]-(related:KnowledgeEntity)
                RETURN collect(DISTINCT {name: e.name, type: e.type}) AS nodes,
                       collect(DISTINCT type(r)) AS relationships
                """;

        var result = neo4jClient.query(nodeCypher)
                .bind(keyword).to("keyword")
                .bind(maxNodes).to("maxNodes")
                .fetch()
                .one();

        if (result.isPresent()) {
            Map<String, Object> row = result.get();

            List<Map<String, Object>> nodesRaw = (List<Map<String, Object>>) row.get("nodes");
            List<NodeInfo> nodes = new ArrayList<>();
            if (nodesRaw != null) {
                for (Map<String, Object> n : nodesRaw) {
                    nodes.add(NodeInfo.builder()
                            .name((String) n.get("name"))
                            .type((String) n.get("type"))
                            .build());
                }
            }

            List<String> relationships = (List<String>) row.get("relationships");

            return SubgraphResultDTO.builder()
                    .nodes(nodes)
                    .relationships(relationships != null ? relationships : Collections.emptyList())
                    .build();
        }

        return SubgraphResultDTO.builder()
                .nodes(Collections.emptyList())
                .relationships(Collections.emptyList())
                .build();
    }

    @Override
    public void mergeEntities(List<EntityMergeDTO> entities) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        for (EntityMergeDTO entity : entities) {
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
                    .bind(entity.getAliases() != null ? entity.getAliases() : Collections.emptyList()).to("aliases")
                    .bind(now).to("now")
                    .run();
        }
        log.debug("Merged {} entities into Neo4j", entities.size());
    }

    @Override
    public void mergeRelations(List<RelationMergeDTO> relations) {
        for (RelationMergeDTO rel : relations) {
            neo4jClient.query("""
                    MATCH (a:KnowledgeEntity {name: $source})
                    MATCH (b:KnowledgeEntity {name: $target})
                    MERGE (a)-[r:RELATED_TO]->(b)
                    ON CREATE SET r.relation = $relType, r.weight = $weight, r.createdAt = datetime()
                    ON MATCH SET r.weight = coalesce($weight, r.weight)
                    """)
                    .bind(rel.getSource()).to("source")
                    .bind(rel.getTarget()).to("target")
                    .bind(rel.getRelationType()).to("relType")
                    .bind(rel.getWeight() != null ? rel.getWeight() : 1.0).to("weight")
                    .run();
        }
        log.debug("Merged {} relations into Neo4j", relations.size());
    }

    @Override
    public void connectChunksToEntities(List<ChunkEntityMappingDTO> chunkEntityMappings) {
        for (ChunkEntityMappingDTO mapping : chunkEntityMappings) {
            neo4jClient.query("""
                    MATCH (c:DocumentChunk {chunkId: $chunkId})
                    MATCH (e:KnowledgeEntity {name: $entityName})
                    MERGE (c)-[r:MENTIONS]->(e)
                    ON CREATE SET r.confidence = $confidence, r.chunkId = $chunkId
                    ON MATCH SET r.confidence = coalesce($confidence, r.confidence)
                    """)
                    .bind(mapping.getChunkId()).to("chunkId")
                    .bind(mapping.getEntityName()).to("entityName")
                    .bind(mapping.getConfidence() != null ? mapping.getConfidence() : 0.8).to("confidence")
                    .run();
        }
        log.debug("Connected {} chunks to entities", chunkEntityMappings.size());
    }

    @Override
    public void createDocumentNode(DocumentPropsDTO props) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        neo4jClient.query("""
                MERGE (d:KnowledgeDocument {docId: $docId})
                ON CREATE SET d.title = $title, d.summary = $summary, d.categoryId = $categoryId,
                              d.authorId = $authorId, d.authorName = $authorName, d.status = $status,
                              d.createdAt = $now, d.updatedAt = $now
                ON MATCH SET d.title = $title, d.summary = $summary, d.categoryId = $categoryId,
                             d.status = $status, d.updatedAt = $now
                """)
                .bind(props.getDocId()).to("docId")
                .bind(props.getTitle()).to("title")
                .bind(props.getSummary() != null ? props.getSummary() : "").to("summary")
                .bind(props.getCategoryId()).to("categoryId")
                .bind(props.getAuthorId()).to("authorId")
                .bind(props.getAuthorName() != null ? props.getAuthorName() : "").to("authorName")
                .bind(props.getStatus() != null ? props.getStatus() : 1).to("status")
                .bind(now).to("now")
                .run();
    }

    @Override
    public void createChunkNode(ChunkPropsDTO props) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        neo4jClient.query("""
                MERGE (c:DocumentChunk {chunkId: $chunkId})
                ON CREATE SET c.docId = $docId, c.content = $content, c.heading = $heading,
                              c.chunkIndex = $chunkIndex, c.totalChunks = $totalChunks, c.createdAt = $now
                ON MATCH SET c.docId = $docId, c.content = $content, c.heading = $heading,
                             c.chunkIndex = $chunkIndex, c.totalChunks = $totalChunks
                """)
                .bind(props.getChunkId()).to("chunkId")
                .bind(props.getDocId()).to("docId")
                .bind(props.getContent() != null ? props.getContent() : "").to("content")
                .bind(props.getHeading() != null ? props.getHeading() : "").to("heading")
                .bind(props.getChunkIndex()).to("chunkIndex")
                .bind(props.getTotalChunks() != null ? props.getTotalChunks() : 1).to("totalChunks")
                .bind(now).to("now")
                .run();
    }

    @Override
    public void createHasChunkRelation(Long docId, String chunkId, int chunkIndex) {
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

    // ==================== Private Helpers ====================

    private static Long toLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return null;
    }
}
