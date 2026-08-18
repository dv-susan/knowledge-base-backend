package com.knowledge.base.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 文件分类请求DTO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Schema(description = "文件分类请求")
public class CategoryDTO {

    /**
     * 分类ID（更新时使用）
     */
    @Schema(description = "分类ID")
    private Long id;

    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 100, message = "分类名称长度不能超过100个字符")
    @Schema(description = "分类名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * 父分类ID（0表示顶级分类）
     */
    @NotNull(message = "父分类ID不能为空")
    @Schema(description = "父分类ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long parentId;

    /**
     * 排序序号
     */
    @Schema(description = "排序序号")
    private Integer sortOrder;

    /**
     * 分类图标
     */
    @Size(max = 255, message = "图标路径长度不能超过255个字符")
    @Schema(description = "分类图标")
    private String icon;

    /**
     * 分类描述
     */
    @Size(max = 500, message = "分类描述长度不能超过500个字符")
    @Schema(description = "分类描述")
    private String description;

    /**
     * 状态：0-禁用，1-启用
     */
    @Schema(description = "状态")
    private Integer status;
}
