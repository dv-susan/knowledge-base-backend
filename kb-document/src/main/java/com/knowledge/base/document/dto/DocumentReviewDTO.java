package com.knowledge.base.document.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文档审核DTO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文档审核参数")
public class DocumentReviewDTO {

    /**
     * 审核记录ID
     */
    @Schema(description = "审核记录ID")
    @NotNull(message = "审核记录ID不能为空")
    private Long reviewId;

    /**
     * 审核结果：1-通过，2-驳回
     */
    @Schema(description = "审核结果：1-通过，2-驳回")
    @NotNull(message = "审核结果不能为空")
    private Integer reviewResult;

    /**
     * 审核意见
     */
    @Schema(description = "审核意见")
    private String reviewComment;
}
