package com.knowledge.base.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI反馈DTO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI反馈参数")
public class FeedbackDTO {

    /**
     * 对话ID
     */
    @Schema(description = "对话ID")
    @NotNull(message = "对话ID不能为空")
    private Long conversationId;

    /**
     * 消息ID
     */
    @Schema(description = "消息ID")
    @NotNull(message = "消息ID不能为空")
    private Long messageId;

    /**
     * 反馈类型（positive/negative）
     */
    @JsonProperty("type")
    @Schema(description = "反馈类型")
    @NotNull(message = "反馈类型不能为空")
    private String feedbackType;

    /**
     * 反馈内容
     */
    @Schema(description = "反馈内容")
    private String feedbackContent;

    /**
     * 评分（1-5分）
     */
    @Schema(description = "评分")
    private Integer rating;
}
