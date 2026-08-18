package com.knowledge.base.foundation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Token验证响应VO（kb-foundation本地DTO）
 *
 * <p>与 kb-user-auth 的 TokenValidateVO 结构保持一致，
 * 作为 Feign 调用的响应类型。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenValidateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;

    private String username;

    private String nickname;

    private String avatar;

    private String email;

    private Integer status;

    /** 角色编码列表，如 ["ROLE_USER", "ROLE_REVIEWER"] */
    private List<String> roles;

    /** Token是否有效 */
    private Boolean valid;
}
