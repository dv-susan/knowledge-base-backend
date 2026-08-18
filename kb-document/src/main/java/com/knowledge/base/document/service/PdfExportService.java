package com.knowledge.base.document.service;

import java.util.List;

/**
 * PDF导出服务接口
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface PdfExportService {

    /**
     * 导出文档为PDF
     *
     * @param documentId 文档ID
     * @return PDF下载URL
     */
    String exportDocumentToPdf(Long documentId);

    /**
     * 导出文档为PDF（直接返回字节数组）
     *
     * @param documentId 文档ID
     * @return PDF文件字节数组
     */
    byte[] exportDocumentToPdfBytes(Long documentId);

    /**
     * 生成PDF文件名
     *
     * @param documentId 文档ID
     * @param title 文档标题
     * @return 文件名
     */
    String generatePdfFileName(Long documentId, String title);

    /**
     * 批量导出文档
     *
     * @param documentIds 文档ID列表（String类型，避免JavaScript精度丢失）
     * @param format 导出格式（pdf / markdown）
     * @return ZIP文件字节数组
     */
    byte[] batchExportDocuments(List<String> documentIds, String format);
}