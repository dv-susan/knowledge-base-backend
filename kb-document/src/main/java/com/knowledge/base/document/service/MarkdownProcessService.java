package com.knowledge.base.document.service;

import java.util.List;
import java.util.Map;

/**
 * Markdown处理服务接口
 *
 * <p>用于处理Markdown内容，包括图片URL替换等</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface MarkdownProcessService {

    /**
     * 处理Markdown内容中的图片
     * <p>自动上传外部图片到rustfs服务器，并替换URL</p>
     *
     * @param content Markdown内容
     * @return 处理后的内容和图片URL映射（原始URL -> 新URL）
     */
    MarkdownProcessResult processImages(String content);

    /**
     * 从Markdown中提取所有图片URL
     *
     * @param content Markdown内容
     * @return 图片URL列表
     */
    List<String> extractImageUrls(String content);

    /**
     * 替换Markdown中的图片URL
     *
     * @param content        Markdown内容
     * @param urlMappings    URL映射（原始URL -> 新URL）
     * @return 替换后的Markdown内容
     */
    String replaceImageUrls(String content, Map<String, String> urlMappings);

    /**
     * 生成内容摘要（HTML格式）
     *
     * @param content     Markdown内容
     * @param maxLength   摘要最大长度
     * @return HTML格式的摘要
     */
    String generateSummary(String content, int maxLength);

    /**
     * Markdown处理结果
     */
    class MarkdownProcessResult {
        /**
         * 处理后的内容
         */
        private String processedContent;

        /**
         * 图片URL映射（原始URL -> 新URL）
         */
        private Map<String, String> urlMappings;

        /**
         * 上传成功的图片数量
         */
        private Integer successCount;

        /**
         * 上传失败的图片数量
         */
        private Integer failureCount;

        public MarkdownProcessResult(String processedContent, Map<String, String> urlMappings,
                                     Integer successCount, Integer failureCount) {
            this.processedContent = processedContent;
            this.urlMappings = urlMappings;
            this.successCount = successCount;
            this.failureCount = failureCount;
        }

        public String getProcessedContent() {
            return processedContent;
        }

        public void setProcessedContent(String processedContent) {
            this.processedContent = processedContent;
        }

        public Map<String, String> getUrlMappings() {
            return urlMappings;
        }

        public void setUrlMappings(Map<String, String> urlMappings) {
            this.urlMappings = urlMappings;
        }

        public Integer getSuccessCount() {
            return successCount;
        }

        public void setSuccessCount(Integer successCount) {
            this.successCount = successCount;
        }

        public Integer getFailureCount() {
            return failureCount;
        }

        public void setFailureCount(Integer failureCount) {
            this.failureCount = failureCount;
        }
    }
}
