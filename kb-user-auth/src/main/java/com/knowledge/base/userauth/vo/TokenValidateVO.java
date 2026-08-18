package com.knowledge.base.userauth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Token验证响应VO
 *
 * <p>供其他微服务（如 kb-foundation）通过 Feign 调用 /auth/validate 时使用，
 * 返回经过验证的用户身份和角色信息。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Token验证响应")
public class TokenValidateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "用户状态（0-禁用，1-启用）")
    private Integer status;

    @Schema(description = "角色编码列表，如 [\"USER\", \"REVIEWER\"]")
    private List<String> roles;

    @Schema(description = "Token是否有效")
    private Boolean valid;
}
