package com.knowledge.base.foundation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 通知DTO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Schema(description = "通知DTO")
public class NotificationDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "通知ID")
    private Long id;

    @NotNull(message = "接收用户ID不能为空")
    @Schema(description = "接收用户ID")
    private Long userId;

    @Schema(description = "用户姓名（冗余字段）")
    private String userName;

    @NotBlank(message = "通知类型不能为空")
    @Schema(description = "通知类型：system/comment/mention/review/like")
    private String notificationType;

    @NotBlank(message = "通知标题不能为空")
    @Schema(description = "通知标题")
    private String title;

    @NotBlank(message = "通知内容不能为空")
    @Schema(description = "通知内容")
    private String content;

    @Schema(description = "跳转链接")
    private String link;

    @Schema(description = "关联类型")
    private String relatedType;

    @Schema(description = "关联ID")
    private Long relatedId;

    @Schema(description = "是否已读：0-未读，1-已读")
    private Integer isRead;
}
