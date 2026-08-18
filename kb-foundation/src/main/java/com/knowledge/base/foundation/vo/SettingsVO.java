package com.knowledge.base.foundation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * 系统设置响应VO
 *
 * <p>将系统配置按业务分组返回给前端，便于设置页面按Tab展示</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "系统设置")
public class SettingsVO implements Serializable {

    @Schema(description = "基本设置")
    private Map<String, Object> basic;

    @Schema(description = "安全设置")
    private Map<String, Object> security;

    @Schema(description = "存储设置")
    private Map<String, Object> storage;

    @Schema(description = "通知设置")
    private Map<String, Object> notification;

    @Schema(description = "AI设置")
    private Map<String, Object> ai;

    @Schema(description = "系统状态")
    private SystemStatusVO status;
}
