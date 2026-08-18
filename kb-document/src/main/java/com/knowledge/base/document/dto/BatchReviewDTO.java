package com.knowledge.base.document.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 批量审核 DTO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "批量审核参数")
public class BatchReviewDTO {

    @NotEmpty(message = "审核任务ID列表不能为空")
    @Schema(description = "审核任务ID列表")
    private List<Long> taskIds;

    @NotBlank(message = "审核结果不能为空")
    @Schema(description = "审核结果：approved-通过，rejected-驳回", example = "approved")
    private String status;

    @Schema(description = "审核意见（驳回时建议填写）")
    private String comment;
}
