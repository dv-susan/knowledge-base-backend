package com.knowledge.base.document.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 标签创建DTO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Schema(description = "标签创建请求")
public class TagCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标签名称
     */
    @Schema(description = "标签名称")
    @NotBlank(message = "标签名称不能为空")
    @Size(max = 50, message = "标签名称长度不能超过50个字符")
    private String tagName;

    /**
     * 标签编码
     */
    @Schema(description = "标签编码")
    @Size(max = 50, message = "标签编码长度不能超过50个字符")
    private String tagCode;

    /**
     * 所属分类ID
     */
    @Schema(description = "所属分类ID")
    private Long categoryId;

    /**
     * 标签类型：0-SYSTEM，1-USER
     */
    @Schema(description = "标签类型")
    private Integer tagType;

    /**
     * 颜色
     */
    @Schema(description = "颜色")
    @Size(max = 20, message = "颜色值长度不能超过20个字符")
    private String color;

    /**
     * 图标
     */
    @Schema(description = "图标")
    @Size(max = 50, message = "图标值长度不能超过50个字符")
    private String icon;
}
