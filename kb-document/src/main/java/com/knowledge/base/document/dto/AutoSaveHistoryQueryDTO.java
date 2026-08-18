package com.knowledge.base.document.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 自动保存历史查询DTO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Schema(description = "自动保存历史查询参数")
public class AutoSaveHistoryQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "文档ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long documentId;

    @Schema(description = "当前页", example = "1")
    private Long current = 1L;

    @Schema(description = "每页大小", example = "20")
    private Long size = 20L;
}
