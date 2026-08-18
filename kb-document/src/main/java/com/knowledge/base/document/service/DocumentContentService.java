package com.knowledge.base.document.service;

import com.knowledge.base.document.entity.mongodb.DocumentContent;

/**
 * 文档内容服务接口
 *
 * <p>管理MongoDB中的文档内容</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface DocumentContentService {

    /**
     * 保存文档内容
     *
     * @param documentId 文档ID
     * @param content    文档内容
     * @return MongoDB文档ID
     */
    String saveContent(Long documentId, String content);

    /**
     * 更新文档内容
     *
     * @param documentId 文档ID
     * @param content    新的文档内容
     * @return 是否更新成功
     */
    Boolean updateContent(Long documentId, String content);

    /**
     * 获取文档内容
     *
     * @param documentId 文档ID
     * @return 文档内容
     */
    DocumentContent getContentByDocumentId(Long documentId);

    /**
     * 根据MongoDB ID获取文档内容
     *
     * @param contentId MongoDB文档ID
     * @return 文档内容
     */
    DocumentContent getContentById(String contentId);

    /**
     * 删除文档内容
     *
     * @param documentId 文档ID
     * @return 是否删除成功
     */
    Boolean deleteContent(Long documentId);

    /**
     * 处理文档内容（上传图片等）
     *
     * @param content 原始内容
     * @return 处理后的内容
     */
    String processContent(String content);
}
