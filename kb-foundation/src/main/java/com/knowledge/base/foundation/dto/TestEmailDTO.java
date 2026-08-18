package com.knowledge.base.foundation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 邮件测试请求DTO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Schema(description = "邮件测试请求")
public class TestEmailDTO {

    @NotBlank(message = "邮箱地址不能为空")
    @Email(message = "请输入合法的邮箱地址")
    @Schema(description = "测试邮箱地址", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
}
