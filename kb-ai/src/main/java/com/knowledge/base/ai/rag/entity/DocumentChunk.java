package com.knowledge.base.ai.rag.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文档分块POJO
 *
 * <p>表示文档被分块后的一个片段，包含原始文档引用信息和分块位置信息。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentChunk {

    /** 块唯一ID（UUID） */
    private String chunkId;

    /** 来源文档ID */
    private Long documentId;

    /** 来源文档标题 */
    private String documentTitle;

    /** 块文本内容（含标题上下文） */
    private String content;

    /** 所属章节标题 */
    private String heading;

    /** 块在文档中的序号（从0开始） */
    private Integer chunkIndex;

    /** 文档总分块数 */
    private Integer totalChunks;

    /** 嵌入向量（1024维） */
    private float[] embedding;

    /** 分类ID */
    private Long categoryId;

    /** 作者ID */
    private Long authorId;

    /** 团队ID */
    private Long teamId;

    /** 文档状态 */
    private Integer docStatus;

    /** 文档发布时间 */
    private String publishTime;

    /** 索引时间 */
    private LocalDateTime indexedAt;
}
