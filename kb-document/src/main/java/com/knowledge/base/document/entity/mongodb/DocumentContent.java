package com.knowledge.base.document.entity.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文档内容实体（MongoDB）
 *
 * <p>将大文本内容存储在MongoDB中，MySQL只存储MongoDB文档ID的引用</p>
 * <p>按照苏三设计</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "document_content")
public class DocumentContent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * MongoDB文档ID（自动生成）
     */
    @Id
    private String id;

    /**
     * 关联的MySQL文档ID（用于关联查询）
     */
    @Indexed
    private Long documentId;

    /**
     * 文档内容（Markdown格式）
     */
    private String content;

    /**
     * 内容长度（字符数）
     */
    private Integer contentLength;

    /**
     * 内容摘要（HTML格式，用于快速预览）
     */
    private String contentSummary;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 版本号（用于内容版本控制）
     */
    private Integer version;

    /**
     * 是否已删除（软删除标记）
     */
    private Boolean deleted;
}
