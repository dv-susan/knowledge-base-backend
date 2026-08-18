package com.knowledge.base.foundation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.knowledge.base.common.config.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 系统配置实体类
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("kb_system_config")
@Schema(description = "系统配置实体")
public class SystemConfig extends BaseEntity {

    @Schema(description = "配置键")
    @TableField("config_key")
    private String configKey;

    @Schema(description = "配置值")
    @TableField("config_value")
    private String configValue;

    @Schema(description = "配置类型：string/number/boolean/json")
    @TableField("config_type")
    private String configType;

    @Schema(description = "配置分类：AI/STORAGE/NOTIFICATION/SECURITY等")
    @TableField("category")
    private String category;

    @Schema(description = "配置描述")
    @TableField("description")
    private String description;

    @Schema(description = "是否公开：0-私有，1-公开")
    @TableField("is_public")
    private Integer isPublic;

}
