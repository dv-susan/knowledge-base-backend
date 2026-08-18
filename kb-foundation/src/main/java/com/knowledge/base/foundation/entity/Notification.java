package com.knowledge.base.foundation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.knowledge.base.common.config.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 系统通知实体类
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("kb_notification")
@Schema(description = "系统通知实体")
public class Notification extends BaseEntity {

    @Schema(description = "接收用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "用户姓名（冗余字段）")
    @TableField("user_name")
    private String userName;

    @Schema(description = "通知类型：system/comment/mention/review/like")
    @TableField("notification_type")
    private String notificationType;

    @Schema(description = "通知标题")
    @TableField("title")
    private String title;

    @Schema(description = "通知内容")
    @TableField("content")
    private String content;

    @Schema(description = "跳转链接")
    @TableField("link")
    private String link;

    @Schema(description = "关联类型")
    @TableField("related_type")
    private String relatedType;

    @Schema(description = "关联ID")
    @TableField("related_id")
    private Long relatedId;

    @Schema(description = "是否已读：0-未读，1-已读")
    @TableField("is_read")
    private Integer isRead;

    @Schema(description = "阅读时间")
    @TableField("read_time")
    private LocalDateTime readTime;

}
