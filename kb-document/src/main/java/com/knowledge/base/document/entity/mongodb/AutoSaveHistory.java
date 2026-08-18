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
 * 自动保存历史快照实体（MongoDB）
 *
 * <p>每次自动保存触发时，异步写入一条不可变快照记录</p>
 * <p>用于自动保存历史查看与回溯</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "document_autosave_history")
public class AutoSaveHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * MongoDB文档ID（自动生成）
     */
    @Id
    private String id;

    /**
     * 关联的MySQL文档ID
     */
    @Indexed
    private Long documentId;

    /**
     * 快照时的文档标题
     */
    private String title;

    /**
     * 完整Markdown内容
     */
    private String content;

    /**
     * 内容预览（前200字符，用于列表快速预览）
     */
    private String contentPreview;

    /**
     * 内容长度（字符数）
     */
    private Integer contentLength;

    /**
     * 触发自动保存的用户ID
     */
    private Long authorId;

    /**
     * 快照保存时间
     */
    private LocalDateTime savedAt;

    /**
     * 是否已删除（软删除标记）
     */
    private Boolean deleted;
}
