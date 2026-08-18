package com.knowledge.base.foundation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 字典DTO
 *
 * <p>按照阿里巴巴Java开发规范设计，用于接收字典类型创建/更新请求参数</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Schema(description = "字典类型请求参数")
public class DictDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字典ID
     */
    @Schema(description = "字典ID", example = "1234567890123456789")
    private Long id;

    /**
     * 字典编码
     */
    @Schema(description = "字典编码", required = true, example = "sys_user_gender")
    @NotBlank(message = "字典编码不能为空")
    @Size(max = 100, message = "字典编码长度不能超过100个字符")
    private String dictCode;

    /**
     * 字典名称
     */
    @Schema(description = "字典名称", required = true, example = "用户性别")
    @NotBlank(message = "字典名称不能为空")
    @Size(max = 100, message = "字典名称长度不能超过100个字符")
    private String dictName;

    /**
     * 字典类型
     */
    @Schema(description = "字典类型", required = true, example = "system")
    @NotBlank(message = "字典类型不能为空")
    @Size(max = 50, message = "字典类型长度不能超过50个字符")
    private String dictType;

    /**
     * 描述
     */
    @Schema(description = "描述", example = "用户性别字典")
    @Size(max = 500, message = "描述长度不能超过500个字符")
    private String description;

    /**
     * 排序
     */
    @Schema(description = "排序", example = "0")
    @NotNull(message = "排序不能为空")
    private Integer sort;

    /**
     * 状态：0-禁用，1-正常
     */
    @Schema(description = "状态", example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;
}
