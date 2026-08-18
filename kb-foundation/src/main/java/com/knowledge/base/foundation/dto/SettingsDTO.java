package com.knowledge.base.foundation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

/**
 * 系统设置更新请求DTO
 *
 * <p>支持按Section批量更新设置项。使用Map结构可灵活扩展，无需每次新增设置项都修改DTO字段</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Schema(description = "设置更新请求")
public class SettingsDTO {

    @NotBlank(message = "设置分组不能为空")
    @Schema(description = "设置分组：basic/security/storage/notification/ai", requiredMode = Schema.RequiredMode.REQUIRED)
    private String section;

    @Schema(description = "设置键值对", requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<String, Object> settings;
}
