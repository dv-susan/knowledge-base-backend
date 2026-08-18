package com.knowledge.base.ai.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文档处理结果VO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文档处理结果")
public class DocumentProcessVO {

    /**
     * 处理类型
     */
    @Schema(description = "处理类型")
    private String processType;

    /**
     * 处理结果内容
     */
    @Schema(description = "处理结果内容")
    private String processedContent;

    /**
     * 是否成功
     */
    @Schema(description = "是否成功")
    private Boolean success;

    /**
     * 消息
     */
    @Schema(description = "消息")
    private String message;

    /**
     * 使用Token数
     */
    @Schema(description = "使用Token数")
    private Integer tokens;

    /**
     * 原始内容
     */
    @Schema(description = "原始内容")
    private String originalContent;
}