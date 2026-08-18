package com.knowledge.base.document.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 批量导出请求DTO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchExportRequest {

    /** 文档ID列表（使用String避免JavaScript精度丢失） */
    @NotEmpty(message = "文档ID列表不能为空")
    private List<String> documentIds;

    /** 导出格式：pdf / markdown */
    @NotNull(message = "导出格式不能为空")
    private String format;
}
