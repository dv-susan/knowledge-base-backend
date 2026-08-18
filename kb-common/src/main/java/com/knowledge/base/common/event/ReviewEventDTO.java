package com.knowledge.base.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文档审核事件 DTO
 *
 * <p>通过 RabbitMQ 在 kb-document（审核逻辑）与 kb-foundation（通知推送）之间传递审核事件，
 * 实现跨模块解耦，避免直接依赖。</p>
 *
 * <p>事件类型（eventType）：
 * <ul>
 *   <li>SUBMITTED — 文档提交审核，推送给所有审核员</li>
 *   <li>APPROVED  — 审核通过，推送给文档作者</li>
 *   <li>REJECTED  — 审核驳回，推送给文档作者</li>
 * </ul>
 * </p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewEventDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 事件类型：SUBMITTED / APPROVED / REJECTED */
    private String eventType;

    /** 文档ID */
    private Long documentId;

    /** 文档标题 */
    private String documentTitle;

    /** 文档作者ID */
    private Long authorId;

    /** 文档作者名称（冗余字段，避免查询） */
    private String authorName;

    /** 审核员ID */
    private Long reviewerId;

    /** 审核员名称 */
    private String reviewerName;

    /** 审核轮次 */
    private Integer reviewRound;

    /** 审核级别（预留多级扩展，默认1） */
    private Integer reviewLevel;

    /** 审核意见（驳回时必填） */
    private String reviewComment;

    /** 事件时间 */
    private LocalDateTime timestamp;
}
