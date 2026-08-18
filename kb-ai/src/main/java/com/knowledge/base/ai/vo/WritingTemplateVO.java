package com.knowledge.base.ai.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 写作模板VO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "写作模板")
public class WritingTemplateVO {

    /**
     * 模板唯一标识
     */
    @Schema(description = "模板唯一标识", example = "tech-solution")
    private String id;

    /**
     * 模板名称
     */
    @Schema(description = "模板名称", example = "技术方案")
    private String name;

    /**
     * 模板描述
     */
    @Schema(description = "模板描述", example = "适用于撰写技术方案文档，包含背景分析、方案设计、实施计划等")
    private String description;

    /**
     * 模板分类
     */
    @Schema(description = "模板分类", example = "技术文档")
    private String category;

    /**
     * 预设提示词/模板内容
     */
    @Schema(description = "预设提示词/模板内容")
    private String prompt;

    /**
     * 推荐的内容类型
     */
    @Schema(description = "推荐的内容类型", example = "article")
    private String suggestedContentType;

    /**
     * 推荐的写作风格
     */
    @Schema(description = "推荐的写作风格", example = "technical")
    private String suggestedStyle;
}
