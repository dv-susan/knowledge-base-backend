package com.knowledge.base.foundation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 字典数据DTO
 *
 * <p>按照阿里巴巴Java开发规范设计，用于接收字典数据创建/更新请求参数</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Schema(description = "字典数据请求参数")
public class DictDataDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字典数据ID
     */
    @Schema(description = "字典数据ID", example = "1234567890123456789")
    private Long id;

    /**
     * 字典ID
     */
    @Schema(description = "字典ID", required = true, example = "1234567890123456789")
    @NotNull(message = "字典ID不能为空")
    private Long dictId;

    /**
     * 字典编码（冗余）
     */
    @Schema(description = "字典编码", example = "sys_user_gender")
    @Size(max = 100, message = "字典编码长度不能超过100个字符")
    private String dictCode;

    /**
     * 字典标签
     */
    @Schema(description = "字典标签", required = true, example = "男")
    @NotBlank(message = "字典标签不能为空")
    @Size(max = 100, message = "字典标签长度不能超过100个字符")
    private String dictLabel;

    /**
     * 字典值
     */
    @Schema(description = "字典值", required = true, example = "1")
    @NotBlank(message = "字典值不能为空")
    @Size(max = 100, message = "字典值长度不能超过100个字符")
    private String dictValue;

    /**
     * 排序
     */
    @Schema(description = "排序", example = "0")
    @NotNull(message = "排序不能为空")
    private Integer dictSort;

    /**
     * 样式类名
     */
    @Schema(description = "样式类名", example = "default")
    @Size(max = 100, message = "样式类名长度不能超过100个字符")
    private String cssClass;

    /**
     * 列表样式
     */
    @Schema(description = "列表样式", example = "primary")
    @Size(max = 100, message = "列表样式长度不能超过100个字符")
    private String listClass;

    /**
     * 是否默认：0-否，1-是
     */
    @Schema(description = "是否默认", example = "0")
    @NotNull(message = "是否默认不能为空")
    private Integer isDefault;

    /**
     * 状态：0-禁用，1-正常
     */
    @Schema(description = "状态", example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;
}
