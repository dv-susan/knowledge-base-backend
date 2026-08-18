package com.knowledge.base.foundation.dto;

import com.knowledge.base.common.result.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 系统配置查询DTO
 *
 * <p>按照阿里巴巴Java开发规范设计，用于系统配置查询条件</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "系统配置查询参数")
public class SystemConfigQueryDTO extends PageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 配置键
     */
    @Schema(description = "配置键", example = "ai.model.name")
    private String configKey;

    /**
     * 配置分类：AI/STORAGE/NOTIFICATION/SECURITY等
     */
    @Schema(description = "配置分类", example = "AI")
    private String category;

    /**
     * 配置类型：string/number/boolean/json
     */
    @Schema(description = "配置类型", example = "string")
    private String configType;

    /**
     * 是否公开：0-私有，1-公开
     */
    @Schema(description = "是否公开", example = "0")
    private Integer isPublic;

    /**
     * 关键词搜索（配置键或描述）
     */
    @Schema(description = "关键词", example = "AI")
    private String keyword;
}
