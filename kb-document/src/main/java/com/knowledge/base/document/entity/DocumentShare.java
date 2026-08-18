package com.knowledge.base.document.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.knowledge.base.common.config.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文档分享实体类
 *
 * <p>存储文档分享信息，包括分享链接、有效期、访问权限等</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("kb_document_share")
public class DocumentShare extends BaseEntity {

    /**
     * 分享ID（唯一标识，用于分享链接）
     */
    private String shareId;

    /**
     * 文档ID
     */
    private Long documentId;

    /**
     * 分享标题
     */
    private String title;

    /**
     * 分享类型（1-公开链接，2-私信分享）
     */
    private Integer shareType;

    /**
     * 分享码（可选，用于增加安全性）
     */
    private String shareCode;

    /**
     * 有效期类型（1-永久，2-限时）
     */
    private Integer expireType;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 访问次数限制（0-不限制）
     */
    private Integer accessLimit;

    /**
     * 已访问次数
     */
    private Integer accessCount;

    /**
     * 是否需要密码（0-否，1-是）
     */
    private Integer requirePassword;

    /**
     * 访问密码（加密存储）
     */
    private String password;

    /**
     * 分享人ID
     */
    private Long sharerId;

    /**
     * 分享人名称
     */
    private String sharerName;

    /**
     * 分享描述
     */
    private String description;

    /**
     * 状态（0-有效，1-已失效，2-已删除）
     */
    private Integer status;

    /**
     * 分享时间
     */
    private LocalDateTime shareTime;
}