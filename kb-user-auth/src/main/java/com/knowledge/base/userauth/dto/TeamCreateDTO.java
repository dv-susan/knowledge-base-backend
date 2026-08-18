package com.knowledge.base.userauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 团队创建DTO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Schema(description = "团队创建请求")
public class TeamCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 团队名称
     */
    @Schema(description = "团队名称")
    @NotBlank(message = "团队名称不能为空")
    @Size(max = 100, message = "团队名称长度不能超过100个字符")
    private String teamName;

    /**
     * 团队编码
     */
    @Schema(description = "团队编码")
    @NotBlank(message = "团队编码不能为空")
    @Size(max = 50, message = "团队编码长度不能超过50个字符")
    private String teamCode;

    /**
     * 团队描述
     */
    @Schema(description = "团队描述")
    @Size(max = 500, message = "团队描述长度不能超过500个字符")
    private String description;

    /**
     * 团队图标
     */
    @Schema(description = "团队图标")
    @Size(max = 50, message = "图标长度不能超过50个字符")
    private String icon;

    /**
     * 父团队ID
     */
    @Schema(description = "父团队ID")
    private Long parentId;

    /**
     * 团队负责人ID
     */
    @Schema(description = "团队负责人ID")
    @NotNull(message = "团队负责人不能为空")
    private Long leaderId;
}
