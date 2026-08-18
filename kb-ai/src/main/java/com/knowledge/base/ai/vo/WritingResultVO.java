package com.knowledge.base.ai.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI写作结果VO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI写作结果")
public class WritingResultVO {

    /**
     * 生成的内容
     */
    @Schema(description = "生成的内容")
    private String content;

    /**
     * 使用的Token数
     */
    @Schema(description = "使用的Token数", example = "856")
    private Integer tokens;

    /**
     * 生成内容的字数
     */
    @Schema(description = "生成内容的字数", example = "1024")
    private Integer wordCount;

    /**
     * 使用的模型名称
     */
    @Schema(description = "使用的模型名称", example = "qwen")
    private String model;
}
