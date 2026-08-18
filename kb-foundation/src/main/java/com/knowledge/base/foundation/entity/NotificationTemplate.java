package com.knowledge.base.foundation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.knowledge.base.common.config.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 通知模板实体类
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("kb_notification_template")
@Schema(description = "通知模板实体")
public class NotificationTemplate extends BaseEntity {

    @Schema(description = "模板编码")
    @TableField("template_code")
    private String templateCode;

    @Schema(description = "模板名称")
    @TableField("template_name")
    private String templateName;

    @Schema(description = "通知类型：EMAIL/SMS/WECHAT/SYSTEM/BROWSER")
    @TableField("notification_type")
    private String notificationType;

    @Schema(description = "模板标题")
    @TableField("title")
    private String title;

    @Schema(description = "模板内容")
    @TableField("content")
    private String content;

    @Schema(description = "模板变量（JSON数组）")
    @TableField("variables")
    private String variables;

    @Schema(description = "模板描述")
    @TableField("description")
    private String description;

    @Schema(description = "是否启用：0-停用，1-启用")
    @TableField("is_active")
    private Integer isActive;

}
