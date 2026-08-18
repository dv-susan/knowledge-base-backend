package com.knowledge.base.document.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 自动保存历史VO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Schema(description = "自动保存历史快照")
public class AutoSaveHistoryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "快照ID（MongoDB _id）")
    private String id;

    @Schema(description = "文档ID")
    private Long documentId;

    @Schema(description = "快照时的文档标题")
    private String title;

    @Schema(description = "内容预览（前200字符，列表接口返回）")
    private String contentPreview;

    @Schema(description = "完整Markdown内容（仅详情接口返回）")
    private String content;

    @Schema(description = "内容长度（字符数）")
    private Integer contentLength;

    @Schema(description = "快照保存时间")
    private LocalDateTime savedAt;
}
