package com.knowledge.base.document.repository.mongodb;

import com.knowledge.base.document.entity.mongodb.AutoSaveHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * 自动保存历史MongoDB Repository
 *
 * @author 苏三
 * @since 1.0.0
 */
@Repository
public interface AutoSaveHistoryRepository extends MongoRepository<AutoSaveHistory, String> {

    /**
     * 分页查询指定文档的自动保存历史（按保存时间倒序）
     *
     * @param documentId 文档ID
     * @param pageable   分页参数
     * @return 分页历史记录
     */
    Page<AutoSaveHistory> findByDocumentIdAndDeletedFalseOrderBySavedAtDesc(Long documentId, Pageable pageable);

    /**
     * 统计指定文档的自动保存历史数量
     *
     * @param documentId 文档ID
     * @return 历史记录数量
     */
    long countByDocumentIdAndDeletedFalse(Long documentId);
}
