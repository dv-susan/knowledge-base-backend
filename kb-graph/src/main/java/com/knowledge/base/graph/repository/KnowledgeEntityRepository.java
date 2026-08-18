package com.knowledge.base.graph.repository;

import com.knowledge.base.graph.entity.node.KnowledgeEntityNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 知识实体图谱Repository
 *
 * @author 苏三
 * @since 1.0.0
 */
@Repository
public interface KnowledgeEntityRepository extends Neo4jRepository<KnowledgeEntityNode, String> {

    /**
     * 按名称模糊搜索实体
     */
    @Query("MATCH (e:KnowledgeEntity) WHERE e.name CONTAINS $keyword OR any(a IN coalesce(e.aliases, []) WHERE a CONTAINS $keyword) RETURN e ORDER BY e.updatedAt DESC LIMIT $limit")
    List<KnowledgeEntityNode> searchByName(@Param("keyword") String keyword, @Param("limit") Integer limit);

    /**
     * 按类型查询实体
     */
    @Query("MATCH (e:KnowledgeEntity) WHERE e.type = $type RETURN e ORDER BY e.updatedAt DESC LIMIT $limit")
    List<KnowledgeEntityNode> findByType(@Param("type") String type, @Param("limit") Integer limit);

    /**
     * 查询实体的相邻实体（1跳范围）
     */
    @Query("MATCH (e:KnowledgeEntity {name: $name})-[r:DEPENDS_ON|USES|CONFIGURES|HAS_PART|RELATED_TO]-(neighbor:KnowledgeEntity) RETURN DISTINCT neighbor, type(r) AS relation ORDER BY neighbor.updatedAt DESC LIMIT $limit")
    List<KnowledgeEntityNode> findNeighbors(@Param("name") String name, @Param("limit") Integer limit);

    /**
     * 查询两实体间的关系类型列表
     */
    @Query("MATCH (a:KnowledgeEntity {name: $source})-[r]->(b:KnowledgeEntity {name: $target}) RETURN type(r) AS relationType")
    List<String> findRelationshipsBetween(@Param("source") String source, @Param("target") String target);

    /**
     * 查询提及指定实体的文档分块
     */
    @Query("MATCH (e:KnowledgeEntity {name: $name})<-[:MENTIONS]-(c:DocumentChunk)-[:HAS_CHUNK]->(d:KnowledgeDocument) WHERE d.status = 1 RETURN c.content AS content, c.heading AS heading, d.title AS docTitle, d.docId AS docId, e.name AS entityName ORDER BY c.chunkIndex LIMIT $limit")
    List<EntityChunkMapping> findMentioningChunks(@Param("name") String name, @Param("limit") Integer limit);

    /**
     * 实体数量统计
     */
    @Query("MATCH (e:KnowledgeEntity) RETURN e.type AS type, count(e) AS count ORDER BY count DESC")
    List<EntityTypeStat> countByType();
}
