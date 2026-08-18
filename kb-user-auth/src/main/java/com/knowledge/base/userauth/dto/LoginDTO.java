package com.knowledge.base.userauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录DTO
 *
 * <p>按照阿里巴巴Java开发规范设计，用于接收用户登录请求参数</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Schema(description = "用户登录请求参数")
public class LoginDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    @Schema(description = "用户名", required = true, example = "admin")
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @Schema(description = "密码", required = true, example = "123456")
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 验证码
     */
    @Schema(description = "验证码", example = "1234")
    private String captcha;

    /**
     * 验证码Key
     */
    @Schema(description = "验证码Key")
    private String captchaKey;
}
