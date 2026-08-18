package com.knowledge.base.document.service.impl;

import com.knowledge.base.common.exception.BusinessException;
import com.knowledge.base.document.entity.mongodb.DocumentContent;
import com.knowledge.base.document.repository.mongodb.DocumentContentRepository;
import com.knowledge.base.document.service.DocumentContentService;
import com.knowledge.base.document.service.MarkdownProcessService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 文档内容服务实现类
 *
 * <p>管理MongoDB中的文档内容</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
public class DocumentContentServiceImpl implements DocumentContentService {

    @Resource
    private DocumentContentRepository documentContentRepository;

    @Resource
    private MarkdownProcessService markdownProcessService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String saveContent(Long documentId, String content) {
        log.info("保存文档内容：documentId={}, contentLength={}", documentId, content != null ? content.length() : 0);

        if (documentId == null) {
            throw new BusinessException("文档ID不能为空");
        }

        if (content == null || content.trim().isEmpty()) {
            throw new BusinessException("文档内容不能为空");
        }

        // 处理内容（上传外部图片等）
        String processedContent = processContent(content);

        // 构建文档内容实体
        DocumentContent documentContent = DocumentContent.builder()
                .documentId(documentId)
                .content(processedContent)
                .contentLength(processedContent.length())
                .contentSummary(generateContentSummary(processedContent))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .version(1)
                .deleted(false)
                .build();

        // 保存到MongoDB
        DocumentContent saved = documentContentRepository.save(documentContent);

        log.info("文档内容保存成功：documentId={}, contentId={}", documentId, saved.getId());

        return saved.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateContent(Long documentId, String content) {
        log.info("更新文档内容：documentId={}", documentId);

        if (documentId == null) {
            throw new BusinessException("文档ID不能为空");
        }

        if (content == null || content.trim().isEmpty()) {
            throw new BusinessException("文档内容不能为空");
        }

        // 查找现有内容
        DocumentContent existContent = documentContentRepository.findByDocumentId(documentId);
        if (existContent == null) {
            // 如果不存在，则创建新内容
            saveContent(documentId, content);
            return true;
        }

        // 处理内容（上传外部图片等）
        String processedContent = processContent(content);

        // 更新内容
        existContent.setContent(processedContent);
        existContent.setContentLength(processedContent.length());
        existContent.setContentSummary(generateContentSummary(processedContent));
        existContent.setUpdatedAt(LocalDateTime.now());
        existContent.setVersion(existContent.getVersion() + 1);

        documentContentRepository.save(existContent);

        log.info("文档内容更新成功：documentId={}, contentId={}", documentId, existContent.getId());

        return true;
    }

    @Override
    public DocumentContent getContentByDocumentId(Long documentId) {
        if (documentId == null) {
            throw new BusinessException("文档ID不能为空");
        }

        DocumentContent content = documentContentRepository.findByDocumentId(documentId);
        if (content == null) {
            throw new BusinessException("文档内容不存在");
        }

        return content;
    }

    @Override
    public DocumentContent getContentById(String contentId) {
        if (contentId == null) {
            throw new BusinessException("内容ID不能为空");
        }

        return documentContentRepository.findById(contentId)
                .orElseThrow(() -> new BusinessException("文档内容不存在"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteContent(Long documentId) {
        log.info("删除文档内容：documentId={}", documentId);

        if (documentId == null) {
            throw new BusinessException("文档ID不能为空");
        }

        // 软删除
        DocumentContent content = documentContentRepository.findByDocumentId(documentId);
        if (content != null) {
            content.setDeleted(true);
            content.setUpdatedAt(LocalDateTime.now());
            documentContentRepository.save(content);
        }

        return true;
    }

    @Override
    public String processContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return content;
        }

        // 标准化 Markdown 内容：移除粘贴/导入内容的公共前导缩进，
        // 防止标题等 Markdown 语法被误解析为缩进代码块。
        String normalized = normalizeMarkdown(content);

        // 处理Markdown中的图片（上传外部图片）
        MarkdownProcessService.MarkdownProcessResult result = markdownProcessService.processImages(normalized);

        if (result.getFailureCount() > 0) {
            log.warn("部分图片上传失败：成功{}个，失败{}个",
                    result.getSuccessCount(), result.getFailureCount());
        }

        return result.getProcessedContent();
    }

    /**
     * 标准化 Markdown 内容，移除粘贴/导入内容中的公共前导缩进。
     *
     * <p>当用户从网页、IDE 或文档中复制内容粘贴到编辑器时，可能附带缩进。
     * CommonMark 规范将 4 个及以上空格开头的行视为缩进代码块，
     * 导致标题（#）等 Markdown 语法被渲染为代码而非标题。</p>
     *
     * <p>此方法类比 Python textwrap.dedent：
     * 找出所有非空行的最小公共缩进，将其从每行开头移除。
     * 如果任一非空行没有前导空白字符，则不做任何修改（说明用户有意为之）。</p>
     *
     * @param text 原始 Markdown 文本
     * @return 去除公共缩进后的 Markdown 文本
     */
    private String normalizeMarkdown(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        String[] lines = text.split("\n", -1);

        // 找出所有非空行的最小缩进
        int minIndent = Integer.MAX_VALUE;
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            int indent = 0;
            while (indent < line.length() && (line.charAt(indent) == ' ' || line.charAt(indent) == '\t')) {
                indent++;
            }
            minIndent = Math.min(minIndent, indent);
        }

        // 没有缩进或无内容，无需处理
        if (minIndent == Integer.MAX_VALUE || minIndent == 0) {
            return text;
        }

        // 移除每行的公共缩进
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.trim().isEmpty()) {
                result.append(line);
            } else {
                int stripLen = Math.min(minIndent, line.length());
                result.append(line.substring(stripLen));
            }
            if (i < lines.length - 1) {
                result.append('\n');
            }
        }

        return result.toString();
    }

    /**
     * 生成内容摘要
     *
     * @param content Markdown内容
     * @return 摘要
     */
    private String generateContentSummary(String content) {
        return markdownProcessService.generateSummary(content, 500);
    }
}
