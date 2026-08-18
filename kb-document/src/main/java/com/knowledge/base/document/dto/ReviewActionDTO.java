package com.knowledge.base.document.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 审核操作 DTO（单条审核）
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "审核操作参数")
public class ReviewActionDTO {

    @NotBlank(message = "审核结果不能为空")
    @Schema(description = "审核结果：approved-通过，rejected-驳回", example = "approved")
    private String status;

    @Schema(description = "审核意见")
    private String comment;
}
