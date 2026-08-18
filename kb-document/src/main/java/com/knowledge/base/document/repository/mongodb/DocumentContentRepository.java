package com.knowledge.base.document.repository.mongodb;

import com.knowledge.base.document.entity.mongodb.DocumentContent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * 文档内容MongoDB Repository
 *
 * <p>提供文档内容的CRUD操作</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Repository
public interface DocumentContentRepository extends MongoRepository<DocumentContent, String> {

    /**
     * 根据文档ID查找内容
     *
     * @param documentId 文档ID
     * @return 文档内容
     */
    DocumentContent findByDocumentId(Long documentId);

    /**
     * 根据文档ID删除内容
     *
     * @param documentId 文档ID
     */
    void deleteByDocumentId(Long documentId);
}
