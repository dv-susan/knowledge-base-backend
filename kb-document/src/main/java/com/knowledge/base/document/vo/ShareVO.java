package com.knowledge.base.document.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文档分享响应VO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文档分享响应信息")
public class ShareVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分享ID（唯一标识）
     */
    @Schema(description = "分享ID")
    private String shareId;

    /**
     * 分享链接
     */
    @Schema(description = "分享链接")
    private String shareUrl;

    /**
     * 文档ID
     */
    @Schema(description = "文档ID")
    private Long documentId;

    /**
     * 分享标题
     */
    @Schema(description = "分享标题")
    private String title;

    /**
     * 分享类型（1-公开链接，2-私信分享）
     */
    @Schema(description = "分享类型（1-公开链接，2-私信分享）")
    private Integer shareType;

    /**
     * 分享类型描述
     */
    @Schema(description = "分享类型描述")
    private String shareTypeDesc;

    /**
     * 有效期类型（1-永久，2-限时）
     */
    @Schema(description = "有效期类型（1-永久，2-限时）")
    private Integer expireType;

    /**
     * 过期时间
     */
    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    /**
     * 是否已过期
     */
    @Schema(description = "是否已过期")
    private Boolean expired;

    /**
     * 是否需要密码
     */
    @Schema(description = "是否需要密码")
    private Boolean requirePassword;

    /**
     * 分享人名称
     */
    @Schema(description = "分享人名称")
    private String sharerName;

    /**
     * 分享时间
     */
    @Schema(description = "分享时间")
    private LocalDateTime shareTime;

    /**
     * 访问次数
     */
    @Schema(description = "访问次数")
    private Integer accessCount;

    /**
     * 分享描述
     */
    @Schema(description = "分享描述")
    private String description;
}