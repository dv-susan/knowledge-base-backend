package com.knowledge.base.graph.service.impl;

import com.knowledge.base.graph.dto.CommunityMemberDTO;
import com.knowledge.base.graph.service.GraphService;
import com.knowledge.base.graph.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 知识图谱Service实现
 *
 * <p>基于 Neo4j Cypher 查询实现知识图谱数据访问，
 * 覆盖节点查询、边查询、路径分析、社区检测等场景。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GraphServiceImpl implements GraphService {

    private final Neo4jClient neo4jClient;

    // ==================== 节点查询 ====================

    @Override
    @Cacheable(value = "graphNodes", key = "#type ?: 'all'")
    public List<GraphNodeVO> getNodes(String type) {
        log.info("获取节点列表，type={}", type);

        String cypher;
        if (type != null && !type.isEmpty()) {
            cypher = "MATCH (n:" + type + ") RETURN n, elementId(n) AS elemId ORDER BY n.name, n.title, n.chunkId LIMIT 500";
        } else {
            cypher = "MATCH (n) RETURN n, elementId(n) AS elemId ORDER BY labels(n)[0], n.name, n.title LIMIT 500";
        }

        try {
            return neo4jClient.query(cypher)
                    .fetchAs(GraphNodeVO.class)
                    .mappedBy((typeSystem, record) -> mapNodeRecord(record))
                    .all()
                    .stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Neo4j节点查询失败，返回空列表: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    // ==================== 边查询 ====================

    @Override
    @Cacheable(value = "graphEdges", key = "#sourceType + '_' + #targetType")
    public List<GraphEdgeVO> getEdges(String sourceType, String targetType) {
        log.info("获取边列表，sourceType={}, targetType={}", sourceType, targetType);

        StringBuilder cypher = new StringBuilder("MATCH (a)-[r]-(b) WHERE 1=1");
        if (sourceType != null && !sourceType.isEmpty()) {
            cypher.append(" AND (a:").append(sourceType).append(" OR b:").append(sourceType).append(")");
        }
        if (targetType != null && !targetType.isEmpty()) {
            cypher.append(" AND (a:").append(targetType).append(" OR b:").append(targetType).append(")");
        }
        cypher.append(" RETURN elementId(r) AS relId, type(r) AS relType,"
                + " elementId(a) AS sourceId, elementId(b) AS targetId,"
                + " coalesce(r.weight, 1.0) AS weight"
                + " ORDER BY relType LIMIT 500");

        try {
            return neo4jClient.query(cypher.toString())
                    .fetchAs(GraphEdgeVO.class)
                    .mappedBy((typeSystem, record) -> {
                        String relId = record.get("relId").asString();
                        String relType = record.get("relType").asString();
                        String sourceId = record.get("sourceId").asString();
                        String targetId = record.get("targetId").asString();
                        double weight = record.get("weight").asDouble();

                        return GraphEdgeVO.builder()
                                .id(relId)
                                .source(sourceId)
                                .target(targetId)
                                .relation(relType)
                                .label(relType)
                                .weight(weight)
                                .build();
                    })
                    .all()
                    .stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Neo4j边查询失败: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    // ==================== 节点关系 ====================

    @Override
    @Cacheable(value = "graphNodeRelations", key = "#nodeId")
    public List<GraphRelationVO> getNodeRelations(String nodeId) {
        log.info("获取节点关系，nodeId={}", nodeId);

        // 支持按 elementId、name 或 docId 查找节点
        String cypher = "MATCH (n)-[r]-(m)"
                + " WHERE elementId(n) = $nodeId"
                + "    OR n.name = $nodeId"
                + "    OR toString(n.docId) = $nodeId"
                + " RETURN n, r, m, elementId(n) AS nId, elementId(m) AS mId"
                + " ORDER BY type(r) LIMIT 100";

        try {
            return neo4jClient.query(cypher)
                    .bind(nodeId).to("nodeId")
                    .fetch()
                    .all()
                    .stream()
                    .map(record -> {
                        try {
                            org.neo4j.driver.types.Node nNode =
                                    (org.neo4j.driver.types.Node) record.get("n");
                            org.neo4j.driver.types.Relationship rRel =
                                    (org.neo4j.driver.types.Relationship) record.get("r");
                            org.neo4j.driver.types.Node mNode =
                                    (org.neo4j.driver.types.Node) record.get("m");
                            String nId = (String) record.get("nId");
                            String mId = (String) record.get("mId");

                            GraphNodeVO sourceNode = buildNodeVO(nNode, nId);
                            GraphNodeVO targetNode = buildNodeVO(mNode, mId);

                            return GraphRelationVO.builder()
                                    .id("rel_" + rRel.elementId())
                                    .sourceNode(sourceNode)
                                    .targetNode(targetNode)
                                    .relationType(rRel.type())
                                    .weight(1.0)
                                    .build();
                        } catch (Exception ex) {
                            log.warn("解析节点关系失败: {}", ex.getMessage());
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Neo4j节点关系查询失败: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    // ==================== 图谱搜索 ====================

    @Override
    @Cacheable(value = "graphSearch", key = "#keyword")
    public List<GraphNodeVO> searchGraph(String keyword) {
        log.info("图谱搜索，keyword={}", keyword);

        // 全文搜索：匹配 entity name/aliases，文档 title，chunk content/heading
        String cypher = "MATCH (n)"
                + " WHERE n.name CONTAINS $kw"
                + "   OR any(alias IN coalesce(n.aliases, []) WHERE alias CONTAINS $kw)"
                + "   OR n.title CONTAINS $kw"
                + "   OR n.content CONTAINS $kw"
                + "   OR n.heading CONTAINS $kw"
                + " RETURN n, elementId(n) AS elemId"
                + " ORDER BY labels(n)[0], n.name, n.title LIMIT 100";

        try {
            return neo4jClient.query(cypher)
                    .bind(keyword).to("kw")
                    .fetchAs(GraphNodeVO.class)
                    .mappedBy((typeSystem, record) -> mapNodeRecord(record))
                    .all()
                    .stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Neo4j图谱搜索失败: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    // ==================== 路径分析 ====================

    @Override
    @Cacheable(value = "graphPath", key = "#sourceId + '_' + #targetId + '_' + #maxDepth")
    @SuppressWarnings("unchecked")
    public List<GraphPathVO> analyzePath(String sourceId, String targetId, Integer maxDepth) {
        log.info("路径分析，sourceId={}, targetId={}, maxDepth={}", sourceId, targetId, maxDepth);
        int depth = (maxDepth != null && maxDepth > 0) ? maxDepth : 5;

        String cypher = "MATCH path = shortestPath((a)-[*1.." + depth + "]-(b))"
                + " WHERE (elementId(a) = $sourceId"
                + "        OR a.name = $sourceId"
                + "        OR toString(a.docId) = $sourceId)"
                + "   AND (elementId(b) = $targetId"
                + "        OR b.name = $targetId"
                + "        OR toString(b.docId) = $targetId)"
                + " RETURN nodes(path) AS pathNodes, relationships(path) AS pathRels,"
                + " length(path) AS pathLength"
                + " LIMIT 10";

        try {
            return neo4jClient.query(cypher)
                    .bind(sourceId).to("sourceId")
                    .bind(targetId).to("targetId")
                    .fetch()
                    .all()
                    .stream()
                    .map(record -> {
                        try {
                            int length = ((Number) record.get("pathLength")).intValue();
                            List<?> pathNodes = (List<?>) record.get("pathNodes");
                            List<?> pathRels = (List<?>) record.get("pathRels");

                            List<GraphNodeVO> nodes = new ArrayList<>();
                            List<GraphEdgeVO> edges = new ArrayList<>();

                            for (int i = 0; i < pathNodes.size(); i++) {
                                org.neo4j.driver.types.Node nodeVal =
                                        (org.neo4j.driver.types.Node) pathNodes.get(i);
                                String nodeType = extractNodeLabelFromDriverNode(nodeVal);
                                String displayName = buildDisplayName(nodeVal, nodeType);
                                String nodeId = nodeVal.elementId();

                                Map<String, Object> nodeProps = new LinkedHashMap<>(nodeVal.asMap());
                                String docId = extractDocumentId(nodeProps);

                                nodes.add(GraphNodeVO.builder()
                                        .id(nodeId)
                                        .name(displayName)
                                        .type(nodeType)
                                        .label(displayName)
                                        .size(resolveNodeSize(nodeType))
                                        .color(getNodeColor(nodeType))
                                        .properties(nodeProps)
                                        .documentId(docId)
                                        .build());
                            }

                            for (int i = 0; i < pathRels.size(); i++) {
                                org.neo4j.driver.types.Relationship relVal =
                                        (org.neo4j.driver.types.Relationship) pathRels.get(i);
                                String relType = relVal.type();

                                edges.add(GraphEdgeVO.builder()
                                        .id("path_edge_" + i)
                                        .source(nodes.get(i).getId())
                                        .target(nodes.get(Math.min(i + 1, nodes.size() - 1)).getId())
                                        .relation(relType)
                                        .label(relType)
                                        .weight(1.0)
                                        .build());
                            }

                            return GraphPathVO.builder()
                                    .nodes(nodes)
                                    .edges(edges)
                                    .length(length)
                                    .weight(1.0)
                                    .build();
                        } catch (Exception ex) {
                            log.warn("解析路径数据失败: {}", ex.getMessage());
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Neo4j路径分析失败: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    // ==================== 社区检测 ====================

    @Override
    @Cacheable(value = "graphCommunity", key = "#algorithm ?: 'default'")
    public List<GraphCommunityVO> detectCommunity(String algorithm) {
        log.info("社区检测，algorithm={}", algorithm);

        // 降级实现：基于节点度数检测社区，按 type 分组
        String cypher = "MATCH (e:KnowledgeEntity)"
                + " OPTIONAL MATCH (e)-[r]-(other:KnowledgeEntity)"
                + " RETURN e.name AS name, e.type AS type, elementId(e) AS elemId, count(r) AS degree"
                + " ORDER BY degree DESC LIMIT 50";

        try {
            List<CommunityMemberDTO> rows = neo4jClient.query(cypher)
                    .fetch()
                    .all()
                    .stream()
                    .map(record -> {
                        String entityName = (String) record.get("name");
                        String elemId = (String) record.get("elemId");
                        return CommunityMemberDTO.builder()
                                .name(entityName != null ? entityName : elemId)
                                .type((String) record.getOrDefault("type", "Unknown"))
                                .degree(record.get("degree") instanceof Number n ? n.longValue() : null)
                                .build();
                    })
                    .collect(Collectors.toList());

            if (rows.isEmpty()) {
                return Collections.emptyList();
            }

            // 按 type 分组作为社区
            Map<String, List<CommunityMemberDTO>> grouped = rows.stream()
                    .collect(Collectors.groupingBy(
                            m -> m.getType() != null ? m.getType() : "Unknown"
                    ));

            List<GraphCommunityVO> communities = new ArrayList<>();
            int idx = 0;
            for (Map.Entry<String, List<CommunityMemberDTO>> entry : grouped.entrySet()) {
                String communityType = entry.getKey();
                List<CommunityMemberDTO> members = entry.getValue();

                List<GraphNodeVO> memberNodes = members.stream()
                        .map(m -> GraphNodeVO.builder()
                                .id(m.getName())
                                .name(m.getName())
                                .type(communityType)
                                .label(m.getName())
                                .size(15)
                                .color(getNodeColor(communityType))
                                .build())
                        .collect(Collectors.toList());

                long totalDegree = members.stream()
                        .mapToLong(CommunityMemberDTO::getDegree)
                        .sum();
                double density = members.size() > 1
                        ? (double) totalDegree / (members.size() * (members.size() - 1))
                        : 0.0;

                communities.add(GraphCommunityVO.builder()
                        .id("community_" + (++idx))
                        .name(communityType + "社区")
                        .memberCount(memberNodes.size())
                        .members(memberNodes)
                        .density(Math.round(density * 100.0) / 100.0)
                        .build());
            }

            return communities;
        } catch (Exception e) {
            log.warn("Neo4j社区检测失败: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    // ==================== 完整图谱数据 ====================

    @Override
    @Cacheable(value = "graphData", key = "#type ?: 'all'")
    public GraphDataVO getGraphData(String type) {
        log.info("获取完整图谱数据，type={}", type);

        List<GraphNodeVO> nodes = getNodes(type);
        List<GraphEdgeVO> edges = getEdges(type, null);

        return GraphDataVO.builder()
                .nodes(nodes)
                .edges(edges)
                .nodeCount(nodes.size())
                .edgeCount(edges.size())
                .build();
    }

    // ==================== 图谱删除 ====================

    @Override
    @CacheEvict(cacheNames = {"graphNodes", "graphEdges", "graphNodeRelations",
            "graphSearch", "graphPath", "graphCommunity", "graphData"}, allEntries = true)
    public void deleteByDocId(Long docId) {
        log.info("删除文档图谱数据：docId={}", docId);
        if (docId == null) return;

        try {
            neo4jClient.query("""
                    MATCH (d:KnowledgeDocument {docId: $docId})
                    OPTIONAL MATCH (d)-[:HAS_CHUNK]->(c:DocumentChunk)
                    OPTIONAL MATCH (c)-[r:MENTIONS]->(e:KnowledgeEntity)
                    DETACH DELETE d, c
                    """)
                    .bind(docId).to("docId")
                    .run();
            log.info("图谱数据已删除：docId={}", docId);
        } catch (Exception e) {
            log.warn("删除图谱数据失败：docId={}, error={}", docId, e.getMessage());
        }
    }

    @Override
    public int cleanupGhostNodes(List<Long> validDocIds) {
        if (validDocIds == null || validDocIds.isEmpty()) {
            log.warn("cleanupGhostNodes: 白名单为空，跳过清理（避免误删全部节点）");
            return 0;
        }
        log.info("开始清理脏图谱节点，白名单大小：{}", validDocIds.size());
        try {
            // 收集要删除的 docId
            List<Long> toDelete = new ArrayList<>();
            neo4jClient.query("MATCH (d:KnowledgeDocument) RETURN d.docId AS docId")
                    .fetch().all()
                    .forEach(r -> {
                        Object v = r.get("docId");
                        Long docId = v instanceof Number ? ((Number) v).longValue() : null;
                        if (docId != null && !validDocIds.contains(docId)) {
                            toDelete.add(docId);
                        }
                    });

            log.info("扫描到 {} 个需要清理的脏图谱节点", toDelete.size());

            // 逐个删除
            for (Long docId : toDelete) {
                deleteByDocId(docId);
            }

            log.info("清理完成，已删除 {} 个脏图谱节点", toDelete.size());
            return toDelete.size();
        } catch (Exception e) {
            log.error("清理脏图谱节点失败：{}", e.getMessage(), e);
            return 0;
        }
    }

    // ==================== 缓存管理 ====================

    @Override
    @CacheEvict(cacheNames = {"graphNodes", "graphEdges", "graphNodeRelations",
            "graphSearch", "graphPath", "graphCommunity", "graphData"}, allEntries = true)
    public void evictAllCaches() {
        log.info("图谱 Redis 缓存已全部清除");
        // No-op: 实际缓存清除由 @CacheEvict 注解完成
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 根据节点类型和已有属性构建可读的显示名称。
     * <p>DocumentChunk 没有 name 属性，需用 heading 或 content；KnowledgeDocument 没有 name 属性，需用 title。</p>
     */
    private static String buildDisplayName(Map<String, Object> nodeProps, String nodeType) {
        if (nodeType == null) {
            return "Unknown";
        }

        return switch (nodeType) {
            case "KnowledgeDocument", "Document" -> {
                String title = (String) nodeProps.getOrDefault("title", "");
                yield !title.isEmpty() ? title : "文档#" + nodeProps.getOrDefault("docId", "?");
            }
            case "DocumentChunk", "Chunk" -> {
                String heading = (String) nodeProps.getOrDefault("heading", "");
                if (!heading.isEmpty()) {
                    yield heading;
                }
                String content = (String) nodeProps.getOrDefault("content", "");
                if (!content.isEmpty()) {
                    yield content.length() > 60 ? content.substring(0, 60) + "..." : content;
                }
                String chunkId = (String) nodeProps.getOrDefault("chunkId", "");
                yield !chunkId.isEmpty() ? "片段#" + chunkId.substring(0, Math.min(chunkId.length(), 8)) : "Chunk";
            }
            case "KnowledgeEntity", "Entity" -> {
                String name = (String) nodeProps.getOrDefault("name", "");
                yield !name.isEmpty() ? name : "Entity#" + nodeProps.getOrDefault("type", "?");
            }
            case "Category" -> {
                yield (String) nodeProps.getOrDefault("name", "Category");
            }
            default -> {
                String name = (String) nodeProps.getOrDefault("name", "");
                if (!name.isEmpty()) yield name;
                String title = (String) nodeProps.getOrDefault("title", "");
                if (!title.isEmpty()) yield title;
                yield nodeType;
            }
        };
    }

    /**
     * 从 Neo4j Driver Node 构建可读显示名称
     */
    private static String buildDisplayName(org.neo4j.driver.types.Node node, String nodeType) {
        return buildDisplayName(node.asMap(), nodeType);
    }

    /**
     * 从 Neo4j Driver Node 构建 GraphNodeVO
     */
    private GraphNodeVO buildNodeVO(org.neo4j.driver.types.Node node, String elementId) {
        String nodeType = extractNodeLabelFromDriverNode(node);
        String displayName = buildDisplayName(node, nodeType);

        Map<String, Object> properties = new LinkedHashMap<>(node.asMap());
        String documentId = extractDocumentId(properties);

        return GraphNodeVO.builder()
                .id(elementId)
                .name(displayName)
                .type(nodeType)
                .label(displayName)
                .size(resolveNodeSize(nodeType))
                .color(getNodeColor(nodeType))
                .properties(properties)
                .documentId(documentId)
                .build();
    }

    /**
     * 映射 Neo4j 节点记录为 GraphNodeVO。
     * <p>使用 elementId 作为唯一 ID，根据节点类型从实际存在的属性构建显示名称。</p>
     */
    private GraphNodeVO mapNodeRecord(org.neo4j.driver.Record record) {
        try {
            var nodeValue = record.get("n");
            var node = nodeValue.asNode();
            String elementId = record.containsKey("elemId")
                    ? record.get("elemId").asString()
                    : node.elementId();
            String nodeType = extractNodeLabelStatic(nodeValue);
            String displayName = buildDisplayName(node, nodeType);

            Map<String, Object> properties = new LinkedHashMap<>(node.asMap());
            String documentId = extractDocumentId(properties);

            return GraphNodeVO.builder()
                    .id(elementId)
                    .name(displayName)
                    .type(nodeType)
                    .label(displayName)
                    .size(resolveNodeSize(nodeType))
                    .color(getNodeColor(nodeType))
                    .properties(properties)
                    .documentId(documentId)
                    .build();
        } catch (Exception e) {
            log.warn("映射节点记录失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从 Neo4j Value 提取节点主标签（fetchAs映射用）
     */
    private static String extractNodeLabelStatic(org.neo4j.driver.Value nodeValue) {
        try {
            var node = nodeValue.asNode();
            for (String label : node.labels()) {
                if (label.startsWith("_")) continue;
                return label;
            }
        } catch (Exception ignored) {
        }
        return "unknown";
    }

    /**
     * 从 Neo4j Driver Node 提取主标签（fetch().all() 方式用）
     */
    private static String extractNodeLabelFromDriverNode(org.neo4j.driver.types.Node node) {
        try {
            for (String label : node.labels()) {
                if (label.startsWith("_")) continue;
                return label;
            }
        } catch (Exception ignored) {
        }
        return "unknown";
    }

    /**
     * 从节点属性中提取文档ID。
     * <p>KnowledgeDocument 和 DocumentChunk 节点都有 docId 属性。</p>
     */
    private static String extractDocumentId(Map<String, Object> nodeProps) {
        Object docId = nodeProps.get("docId");
        if (docId == null) return null;
        return String.valueOf(docId);
    }

    /**
     * 根据节点类型获取可视化颜色
     */
    private String getNodeColor(String nodeType) {
        if (nodeType == null) return "#bfbfbf";
        return switch (nodeType) {
            case "KnowledgeDocument", "Document" -> "#1890ff";
            case "KnowledgeEntity", "Entity" -> "#52c41a";
            case "DocumentChunk", "Chunk" -> "#faad14";
            case "Category" -> "#722ed1";
            case "Author", "User" -> "#eb2f96";
            default -> "#bfbfbf";
        };
    }

    /**
     * 根据节点类型确定可视化尺寸
     */
    private int resolveNodeSize(String nodeType) {
        if (nodeType == null) return 15;
        return switch (nodeType) {
            case "KnowledgeDocument", "Document" -> 30;
            case "KnowledgeEntity", "Entity" -> 25;
            case "DocumentChunk", "Chunk" -> 20;
            case "Category" -> 28;
            case "Author", "User" -> 22;
            default -> 15;
        };
    }
}
