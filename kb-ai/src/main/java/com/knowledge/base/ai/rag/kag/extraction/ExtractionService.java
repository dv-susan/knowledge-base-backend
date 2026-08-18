package com.knowledge.base.ai.rag.kag.extraction;

import com.knowledge.base.ai.dto.kag.extraction.ExtractionResult;

import java.util.List;

/**
 * 实体关系抽取服务接口
 *
 * <p>从文档分块中抽取知识实体和关系，构建知识图谱的基础数据。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface ExtractionService {

    /**
     * 从单个文档分块抽取实体和关系
     *
     * @param content       分块文本内容
     * @param heading       所属章节标题
     * @param docId         来源文档ID
     * @param documentTitle 文档标题
     * @return 抽取结果
     */
    ExtractionResult extract(String content, String heading, Long docId, String documentTitle);

    /**
     * 批量抽取（一次LLM调用处理多个分块，减少API调用次数）
     *
     * @param contents 分块内容列表，每个元素为 {content, heading, docId, documentTitle}
     * @return 抽取结果列表
     */
    List<ExtractionResult> extractBatch(List<ExtractionInput> contents);

    /**
     * 抽取输入封装
     */
    record ExtractionInput(String content, String heading, Long docId, String documentTitle) {}
}
