package com.knowledge.base.ai.rag.service;

import com.knowledge.base.ai.vo.ReindexProgressVO;

import java.util.List;

/**
 * 重建索引服务接口
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface ReindexService {

    /**
     * 重建所有已发布文档
     */
    String reindexAll();

    /**
     * 重建指定文档
     */
    String reindexByDocId(Long documentId);

    /**
     * 批量重建文档
     */
    String reindexBatch(List<Long> documentIds);

    /**
     * 获取重建进度
     */
    ReindexProgressVO getProgress(String taskId);

    /**
     * 删除指定文档的向量索引
     */
    String deleteByDocId(Long documentId);

    /**
     * 批量删除指定文档的向量索引
     */
    String deleteByDocIds(List<Long> documentIds);
}
