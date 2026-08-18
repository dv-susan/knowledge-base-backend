package com.knowledge.base.ai.rag.kag.retrieval;

import com.knowledge.base.ai.vo.RagSearchResultVO;

import java.util.List;

/**
 * 混合检索服务接口
 *
 * <p>融合 RAG（文本向量检索）和 KAG（知识图谱推理）两种检索模式，
 * 提供更全面的知识检索能力。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface HybridRetrievalService {

    /**
     * 混合检索结果
     */
    record HybridResult(
            /** RAG 文本检索结果 */
            List<RagSearchResultVO> ragResults,
            /** KAG 图谱检索上下文 */
            GraphContext kagContext,
            /** 融合后的最终结果 */
            List<RagSearchResultVO> fusedResults,
            /** 是否包含图谱增强 */
            boolean knowledgeGraphEnhanced
    ) {}

    /**
     * 执行混合检索（RAG + KAG并行 → 合并去重 → 重排序）
     *
     * @param query         用户查询
     * @param topK          最终返回结果数
     * @param enableRerank  是否启用LLM重排序
     * @param enableKAG     是否启用知识图谱增强
     * @return 混合检索结果
     */
    HybridResult retrieveHybrid(String query, int topK, boolean enableRerank, boolean enableKAG);
}
