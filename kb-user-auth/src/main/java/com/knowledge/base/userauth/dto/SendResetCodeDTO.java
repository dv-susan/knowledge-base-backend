package com.knowledge.base.userauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 发送重置密码验证码请求
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Schema(description = "发送重置密码验证码请求")
public class SendResetCodeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "注册邮箱", required = true)
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
}
