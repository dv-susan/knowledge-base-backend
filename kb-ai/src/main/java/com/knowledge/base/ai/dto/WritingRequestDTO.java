package com.knowledge.base.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI写作请求参数
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI写作请求参数")
public class WritingRequestDTO {

    /**
     * 写作主题/标题
     */
    @Schema(description = "写作主题/标题", example = "如何写好一份技术方案")
    @NotBlank(message = "写作主题不能为空")
    private String topic;

    /**
     * 写作要求/补充说明
     */
    @Schema(description = "写作要求/补充说明", example = "面向技术管理者的技术方案，包含背景、目标、方案设计、实施计划")
    private String requirements;

    /**
     * 内容类型：article-文章, report-报告, documentation-技术文档, email-邮件, announcement-公告
     */
    @Schema(description = "内容类型", example = "article",
            allowableValues = {"article", "report", "documentation", "email", "announcement"})
    private String contentType;

    /**
     * 写作风格：formal-正式, casual-轻松, technical-技术, creative-创意, academic-学术
     */
    @Schema(description = "写作风格", example = "formal",
            allowableValues = {"formal", "casual", "technical", "creative", "academic"})
    private String style;

    /**
     * 语气：neutral-中性, enthusiastic-热情, serious-严肃, friendly-友好, authoritative-权威
     */
    @Schema(description = "语气", example = "neutral",
            allowableValues = {"neutral", "enthusiastic", "serious", "friendly", "authoritative"})
    private String tone;

    /**
     * 期望字数
     */
    @Schema(description = "期望字数", example = "1000")
    private Integer length;

    /**
     * 参考/现有内容（用于扩写、优化、续写场景）
     */
    @Schema(description = "参考/现有内容（用于扩写、优化、续写场景）")
    private String existingContent;

    /**
     * 操作类型：generate-生成, expand-扩写, optimize-优化, continue-续写
     */
    @Schema(description = "操作类型", example = "generate",
            allowableValues = {"generate", "expand", "optimize", "continue"})
    @NotBlank(message = "操作类型不能为空")
    private String actionType;

    /**
     * 模板ID（使用预设模板时传入）
     */
    @Schema(description = "模板ID（使用预设模板时传入）", example = "tech-solution")
    private String templateId;

    /**
     * 使用的模型名称，默认使用配置的默认模型
     */
    @Schema(description = "使用的模型名称，默认使用配置的默认模型", example = "qwen")
    private String model;
}
