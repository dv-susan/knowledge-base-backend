package com.knowledge.base.userauth.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 注册响应VO
 *
 * <p>当用户注册时提供了邮箱，则需要进行邮箱验证才能激活账号；
 * 未提供邮箱时，直接注册成功并自动登录。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterVO implements Serializable {

    /**
     * 新注册用户ID
     */
    private Long userId;

    /**
     * 是否需要邮箱验证
     */
    private boolean emailVerificationRequired;

    /**
     * 提示消息
     */
    private String message;

    /**
     * 登录信息（仅在无需邮箱验证时有值）
     */
    private LoginVO loginInfo;
}
