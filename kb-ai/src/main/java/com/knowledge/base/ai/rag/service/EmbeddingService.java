package com.knowledge.base.ai.rag.service;

import java.util.List;

/**
 * 嵌入服务接口
 *
 * <p>将文本转换为向量嵌入（embedding），支持单条和批量操作，支持Redis缓存。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface EmbeddingService {

    /**
     * 生成单条文本的向量嵌入
     *
     * @param text 输入文本
     * @return 1024维浮点向量
     */
    float[] embed(String text);

    /**
     * 批量生成向量嵌入
     *
     * @param texts 输入文本列表
     * @return 对应的向量列表
     */
    List<float[]> embedBatch(List<String> texts);
}
