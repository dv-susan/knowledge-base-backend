package com.knowledge.base.ai.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 引用来源VO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "引用来源")
public class CitationVO {

    @Schema(description = "引用编号（与回答中的 [1] [2] 对应）")
    private int index;

    @Schema(description = "来源文档ID")
    private Long documentId;

    @Schema(description = "来源文档标题")
    private String documentTitle;

    @Schema(description = "引用片段摘要")
    private String excerpt;

    @Schema(description = "相关度得分")
    private double relevanceScore;
}
