package com.knowledge.base.ai.rag.service;

import com.knowledge.base.ai.rag.entity.DocumentChunk;
import com.knowledge.base.ai.vo.RagSearchResultVO;

import java.util.List;

/**
 * 向量索引服务接口
 *
 * <p>管理文档块的ES索引（写入、删除、混合搜索），
 * 支持 BM25关键词搜索 + kNN向量搜索 + RRF融合。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface VectorIndexService {

    /**
     * 批量索引文档块
     *
     * @param chunks 文档块列表
     */
    void indexChunks(List<DocumentChunk> chunks);

    /**
     * 按文档ID删除所有已索引的块
     *
     * @param documentId 文档ID
     */
    void deleteByDocId(Long documentId);

    /**
     * 混合搜索（BM25 + kNN + RRF融合）
     *
     * @param queryText          查询文本
     * @param queryEmbedding     查询向量
     * @param topK               返回Top-K
     * @param hybridTopK         BM25和kNN各自返回的候选数
     * @param rrfC               RRF常数
     * @return 融合排序后的搜索结果
     */
    List<RagSearchResultVO> searchHybrid(String queryText, float[] queryEmbedding,
                                          int topK, int hybridTopK, int rrfC);

    /**
     * 检查kb_chunk索引是否存在
     */
    boolean indexExists();

    /**
     * 创建kb_chunk索引（如果不存在）
     */
    void createIndexIfNotExists();

    /**
     * 删除整个kb_chunk索引
     */
    void dropIndex();
}
