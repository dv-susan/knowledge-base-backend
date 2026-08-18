package com.knowledge.base.userauth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 用户登录响应VO
 *
 * <p>按照阿里巴巴Java开发规范设计，用于返回用户登录成功后的信息</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户登录响应信息")
public class LoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 访问令牌
     */
    @Schema(description = "访问令牌")
    private String accessToken;

    /**
     * 刷新令牌
     */
    @Schema(description = "刷新令牌")
    private String refreshToken;

    /**
     * 令牌类型
     */
    @Schema(description = "令牌类型")
    private String tokenType;

    /**
     * 过期时间（秒）
     */
    @Schema(description = "过期时间（秒）")
    private Long expiresIn;

    /**
     * 用户信息
     */
    @Schema(description = "用户信息")
    private UserInfo userInfo;

    /**
     * 用户信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "用户信息")
    public static class UserInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 用户ID
         */
        @Schema(description = "用户ID")
        private Long userId;

        /**
         * 用户名
         */
        @Schema(description = "用户名")
        private String username;

        /**
         * 昵称
         */
        @Schema(description = "昵称")
        private String nickname;

        /**
         * 邮箱
         */
        @Schema(description = "邮箱")
        private String email;

        /**
         * 手机号
         */
        @Schema(description = "手机号")
        private String phone;

        /**
         * 头像URL
         */
        @Schema(description = "头像URL")
        private String avatar;

        /**
         * 性别
         */
        @Schema(description = "性别")
        private Integer gender;

        /**
         * 角色列表
         */
        @Schema(description = "角色列表")
        private List<String> roles;

        /**
         * 权限列表
         */
        @Schema(description = "权限列表")
        private List<String> permissions;
    }
}
