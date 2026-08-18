package com.knowledge.base.userauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 重置密码请求
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Schema(description = "重置密码请求")
public class ResetPasswordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "注册邮箱", required = true)
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "验证码", required = true)
    @NotBlank(message = "验证码不能为空")
    private String code;

    @Schema(description = "新密码", required = true)
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度必须在6-50个字符之间")
    private String newPassword;
}
