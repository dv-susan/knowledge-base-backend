package com.knowledge.base.document.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件解析Service
 *
 * <p>将上传的 PDF / Word / Excel / PPT / 纯文本文件解析为 Markdown 文本，
 * 解析后的内容可直接存入知识库，走后续的 RAG / KAG 管线。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface FileParserService {

    /**
     * 解析文件内容，返回 Markdown 格式文本
     *
     * @param file 上传的文件
     * @return 解析后的文本内容
     * @throws Exception 解析异常时抛出
     */
    String parse(MultipartFile file) throws Exception;

    /**
     * 判断文件扩展名是否支持解析
     *
     * @param extension 文件扩展名（不含点，如 "pdf", "docx"）
     * @return true = 支持解析
     */
    boolean isSupported(String extension);
}
