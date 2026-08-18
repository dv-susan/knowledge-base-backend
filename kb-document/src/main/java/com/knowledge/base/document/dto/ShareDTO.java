package com.knowledge.base.document.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 文档分享请求DTO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文档分享请求参数")
public class ShareDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文档ID
     */
    @Schema(description = "文档ID", required = true)
    @NotNull(message = "文档ID不能为空")
    private Long documentId;

    /**
     * 分享类型（1-公开链接，2-私信分享）
     */
    @Schema(description = "分享类型（1-公开链接，2-私信分享）", example = "1")
    @Builder.Default
    private Integer shareType = 1;

    /**
     * 有效期类型（1-永久，2-限时）
     */
    @Schema(description = "有效期类型（1-永久，2-限时）", example = "1")
    @Builder.Default
    private Integer expireType = 1;

    /**
     * 过期时间（限时分享时必填）
     */
    @Schema(description = "过期时间")
    private String expireTime;

    /**
     * 访问次数限制（0-不限制）
     */
    @Schema(description = "访问次数限制（0-不限制）", example = "0")
    @Builder.Default
    private Integer accessLimit = 0;

    /**
     * 是否需要密码（0-否，1-是）
     */
    @Schema(description = "是否需要密码（0-否，1-是）", example = "0")
    @Builder.Default
    private Integer requirePassword = 0;

    /**
     * 访问密码
     */
    @Schema(description = "访问密码")
    private String password;

    /**
     * 分享描述
     */
    @Schema(description = "分享描述")
    private String description;
}