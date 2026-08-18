package com.knowledge.base.ai.rag.service;

import com.knowledge.base.ai.vo.RagSearchResultVO;

import java.util.List;

/**
 * RAG检索服务接口
 *
 * <p>编排检索流程：Query嵌入 → 混合检索 → 重排序 → 返回结果</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface RagRetrievalService {

    /**
     * RAG检索
     *
     * @param query        查询文本
     * @param topK         返回Top-K
     * @param enableRerank 是否启用LLM重排序
     * @return 检索结果列表
     */
    List<RagSearchResultVO> retrieve(String query, int topK, boolean enableRerank);
}
