package com.knowledge.base.ai.rag.kag.retrieval;

/**
 * KAG 图谱检索服务接口
 *
 * <p>基于 Neo4j 知识图谱进行多跳推理检索：
 * 1. 从用户查询中识别实体
 * 2. 在Neo4j中进行多跳遍历发现关联实体和推理路径
 * 3. 反向查找关联的文档文本块
 * 4. 返回结构化的 GraphContext</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface KAGRetrievalService {

    /**
     * 基于查询文本从知识图谱中检索结构化知识
     *
     * @param query       用户查询文本
     * @param maxEntities 最大匹配实体数
     * @param maxHops     最大跳数
     * @param maxChunks   最大返回文本块数
     * @return 图谱检索上下文
     */
    GraphContext retrieveGraphContext(String query, int maxEntities, int maxHops, int maxChunks);

    /**
     * 使用默认参数的检索
     *
     * @param query 用户查询文本
     * @return 图谱检索上下文
     */
    GraphContext retrieveGraphContext(String query);
}
