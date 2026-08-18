package com.knowledge.base.document.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文档版本恢复DTO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文档版本恢复参数")
public class DocumentVersionRestoreDTO {

    /**
     * 版本ID
     */
    @Schema(description = "版本ID")
    @NotNull(message = "版本ID不能为空")
    private Long versionId;

    /**
     * 恢复原因
     */
    @Schema(description = "恢复原因")
    private String reason;
}
