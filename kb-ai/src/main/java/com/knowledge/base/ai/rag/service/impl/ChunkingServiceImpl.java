package com.knowledge.base.ai.rag.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson2.JSON;
import com.knowledge.base.ai.config.RagProperties;
import com.knowledge.base.ai.rag.entity.DocumentChunk;
import com.knowledge.base.ai.rag.service.ChunkingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文档分块实现
 *
 * <p>采用段落感知 + 固定大小的混合分块策略：
 * <ol>
 *   <li>按 Markdown 标题（# ## ###）划分章节</li>
 *   <li>每个章节内按空行（\n\n）分段落</li>
 *   <li>段落按token数合并/分割，达到目标chunkSize</li>
 *   <li>每个块包含最近的标题作为上下文</li>
 * </ol></p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChunkingServiceImpl implements ChunkingService {

    private final RagProperties ragProperties;

    private static final Pattern HEADING_PATTERN = Pattern.compile("^(#{1,6})\\s+(.+)$", Pattern.MULTILINE);

    /** 约1个token = 0.75个中文字符，取保守估计 1 token ≈ 2 字符（中英文混合） */
    private static final double CHARS_PER_TOKEN = 2.0;

    /** {@inheritDoc} */
    @Override
    public List<DocumentChunk> chunk(String content, Long documentId, String documentTitle,
                                      Long categoryId, Long authorId, Long teamId, Integer docStatus,
                                      String publishTime) {
        if (content == null || content.isEmpty()) {
            log.warn("文档内容为空，跳过分块：documentId={}", documentId);
            return List.of();
        }

        int chunkSize = ragProperties.getChunking().getChunkSize();
        int chunkOverlap = ragProperties.getChunking().getChunkOverlap();
        int maxChars = (int) (chunkSize * CHARS_PER_TOKEN);
        int overlapChars = (int) (chunkOverlap * CHARS_PER_TOKEN);

        List<Section> sections = splitByHeadings(content);
        List<DocumentChunk> chunks = new ArrayList<>();

        for (Section section : sections) {
            List<String> paragraphs = splitParagraphs(section.content);
            List<String> merged = mergeParagraphs(paragraphs, maxChars);
            for (int i = 0; i < merged.size(); i++) {
                String chunkContent = (section.heading != null ? section.heading + "\n\n" : "") + merged.get(i);

                // 添加重叠：从上一块的末尾取 overlapChars 个字符
                if (i > 0 && overlapChars > 0) {
                    String prevContent = merged.get(i - 1);
                    if (prevContent.length() > overlapChars) {
                        chunkContent = prevContent.substring(prevContent.length() - overlapChars) + "\n" + chunkContent;
                    }
                }

                chunks.add(DocumentChunk.builder()
                        .chunkId(DigestUtil.sha256Hex(documentId + ":" + chunks.size()))
                        .documentId(documentId)
                        .documentTitle(documentTitle)
                        .content(chunkContent)
                        .heading(section.heading)
                        .chunkIndex(chunks.size())
                        .categoryId(categoryId)
                        .authorId(authorId)
                        .teamId(teamId)
                        .docStatus(docStatus)
                        .publishTime(publishTime)
                        .build());
            }
        }

        // 设置 totalChunks
        final int total = chunks.size();
        chunks.forEach(c -> c.setTotalChunks(total));

        log.debug("文档分块完成：documentId={}, totalChunks={}", documentId, total);
        return chunks;
    }

    /**
     * 按 Markdown 标题划分章节
     */
    private List<Section> splitByHeadings(String content) {
        List<Section> sections = new ArrayList<>();
        Matcher m = HEADING_PATTERN.matcher(content);

        int lastEnd = 0;
        String currentHeading = null;

        while (m.find()) {
            if (lastEnd < m.start()) {
                String sectionContent = content.substring(lastEnd, m.start()).trim();
                if (!sectionContent.isEmpty()) {
                    sections.add(new Section(currentHeading, sectionContent));
                }
            }
            currentHeading = m.group(2);
            lastEnd = m.end();
        }

        // 最后一段
        if (lastEnd < content.length()) {
            String sectionContent = content.substring(lastEnd).trim();
            if (!sectionContent.isEmpty()) {
                sections.add(new Section(currentHeading, sectionContent));
            }
        }

        // 如果没有找到任何标题，整个内容作为一个无标题章节
        if (sections.isEmpty()) {
            sections.add(new Section(null, content.trim()));
        }

        return sections;
    }

    /**
     * 按空行分段落
     */
    private List<String> splitParagraphs(String content) {
        List<String> paragraphs = new ArrayList<>();
        for (String para : content.split("\n\n")) {
            String trimmed = para.trim();
            if (!trimmed.isEmpty()) {
                paragraphs.add(trimmed);
            }
        }
        return paragraphs;
    }

    /**
     * 合并段落达到目标大小
     */
    private List<String> mergeParagraphs(List<String> paragraphs, int maxChars) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (String paragraph : paragraphs) {
            // 如果当前段落本身就超过maxChars，按句子分割
            if (paragraph.length() > maxChars) {
                if (current.length() > 0) {
                    result.add(current.toString().trim());
                    current = new StringBuilder();
                }
                List<String> subParts = splitLongParagraph(paragraph, maxChars);
                result.addAll(subParts);
                continue;
            }

            if (current.length() + paragraph.length() + 2 <= maxChars) {
                if (current.length() > 0) {
                    current.append("\n\n");
                }
                current.append(paragraph);
            } else {
                if (current.length() > 0) {
                    result.add(current.toString().trim());
                }
                current = new StringBuilder(paragraph);
            }
        }

        if (current.length() > 0) {
            result.add(current.toString().trim());
        }

        return result;
    }

    /**
     * 将超长段落按句子分割
     */
    private List<String> splitLongParagraph(String paragraph, int maxChars) {
        List<String> result = new ArrayList<>();
        // 按句号、问号、感叹号、换行分割
        String[] sentences = paragraph.split("(?<=[。！？\\.!?\\n])");
        StringBuilder current = new StringBuilder();

        for (String sentence : sentences) {
            String trimmed = sentence.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            if (current.length() + trimmed.length() > maxChars) {
                if (current.length() > 0) {
                    result.add(current.toString().trim());
                    current = new StringBuilder();
                }
                // 如果单个句子就超长，强制截断
                if (trimmed.length() > maxChars) {
                    for (int i = 0; i < trimmed.length(); i += maxChars) {
                        int end = Math.min(i + maxChars, trimmed.length());
                        result.add(trimmed.substring(i, end));
                    }
                } else {
                    current = new StringBuilder(trimmed);
                }
            } else {
                if (current.length() > 0) {
                    current.append(" ");
                }
                current.append(trimmed);
            }
        }

        if (current.length() > 0) {
            result.add(current.toString().trim());
        }

        return result;
    }

    /**
     * 章节内部类
     */
    private record Section(String heading, String content) {}
}
