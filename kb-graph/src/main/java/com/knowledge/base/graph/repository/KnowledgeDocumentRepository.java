package com.knowledge.base.graph.repository;

import com.knowledge.base.graph.entity.node.KnowledgeDocumentNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 知识文档图谱Repository
 *
 * @author 苏三
 * @since 1.0.0
 */
@Repository
public interface KnowledgeDocumentRepository extends Neo4jRepository<KnowledgeDocumentNode, Long> {

    /**
     * 查询所有已发布的文档
     */
    @Query("MATCH (d:KnowledgeDocument) WHERE d.status = 1 RETURN d ORDER BY d.updatedAt DESC")
    List<KnowledgeDocumentNode> findAllPublished();

    /**
     * 查询指定分类下的已发布文档
     */
    @Query("MATCH (d:KnowledgeDocument) WHERE d.categoryId = $categoryId AND d.status = 1 RETURN d")
    List<KnowledgeDocumentNode> findByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 按标题关键词搜索已发布文档
     */
    @Query("MATCH (d:KnowledgeDocument) WHERE d.status = 1 AND d.title CONTAINS $keyword RETURN d")
    List<KnowledgeDocumentNode> searchByTitle(@Param("keyword") String keyword);

    /**
     * 删除指定文档及其所有关联
     */
    @Query("MATCH (d:KnowledgeDocument {docId: $docId}) DETACH DELETE d")
    void deleteByDocId(@Param("docId") Long docId);

    /**
     * 查询文档数
     */
    @Query("MATCH (d:KnowledgeDocument) WHERE d.status = 1 RETURN count(d)")
    long countPublished();
}
