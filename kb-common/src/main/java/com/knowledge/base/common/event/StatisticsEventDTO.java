package com.knowledge.base.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 统计事件 DTO
 *
 * <p>通过 RabbitMQ 在业务服务与统计服务之间传递的事件消息</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsEventDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 事件类型: VIEW / LIKE / FAVORITE / COMMENT / CREATE */
    private String eventType;

    /** 用户ID（未登录时为 null） */
    private Long userId;

    /** 用户名称 */
    private String userName;

    /** 文档ID */
    private Long documentId;

    /** 文档标题 */
    private String documentTitle;

    /** IP地址 */
    private String ipAddress;

    /** 用户代理 */
    private String userAgent;

    /** 事件发生时间 */
    private LocalDateTime timestamp;
}
