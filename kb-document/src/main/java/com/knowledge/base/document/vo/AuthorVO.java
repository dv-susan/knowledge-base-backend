package com.knowledge.base.document.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 作者信息VO
 *
 * <p>用于返回文档作者详细信息</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Schema(description = "作者信息")
public class AuthorVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 作者ID
     */
    @Schema(description = "作者ID")
    private Long id;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;

    /**
     * 头像URL
     */
    @Schema(description = "头像URL")
    private String avatar;

    /**
     * 职位
     */
    @Schema(description = "职位")
    private String position;
}
