package com.knowledge.base.userauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 权限DTO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Schema(description = "权限信息")
public class PermissionDTO {

    @Schema(description = "权限ID")
    private Long id;

    @NotBlank(message = "权限名称不能为空")
    @Schema(description = "权限名称")
    private String name;

    @NotBlank(message = "权限编码不能为空")
    @Schema(description = "权限编码")
    private String code;

    @Schema(description = "权限类型（menu、button、api）")
    private String type;

    @Schema(description = "父权限ID")
    private Long parentId;

    @Schema(description = "菜单URL")
    private String menuUrl;

    @Schema(description = "接口URL")
    private String apiUrl;

    @Schema(description = "请求方法")
    private String method;

    @Schema(description = "权限描述")
    private String description;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "状态（0-禁用，1-启用）")
    private Integer status;
}
