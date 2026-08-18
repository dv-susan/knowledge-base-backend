package com.knowledge.base.ai.rag.service;

import com.knowledge.base.ai.rag.entity.DocumentChunk;

import java.util.List;

/**
 * 文档分块服务接口
 *
 * <p>将长文档按照策略分割为适合嵌入和检索的小块。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface ChunkingService {

    /**
     * 将Markdown文档分块
     *
     * @param content       Markdown格式的文档内容
     * @param documentId    文档ID
     * @param documentTitle 文档标题
     * @param categoryId    分类ID
     * @param authorId      作者ID
     * @param teamId        团队ID
     * @param docStatus     文档状态
     * @param publishTime   文档发布时间
     * @return 分块列表
     */
    List<DocumentChunk> chunk(String content, Long documentId, String documentTitle,
                              Long categoryId, Long authorId, Long teamId, Integer docStatus,
                              String publishTime);
}
