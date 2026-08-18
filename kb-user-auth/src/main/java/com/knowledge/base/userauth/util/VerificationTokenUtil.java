package com.knowledge.base.userauth.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 验证令牌工具类
 *
 * <p>用于生成和验证账户激活令牌</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Component
public class VerificationTokenUtil {

    @Value("${app.activation-token-expiry-hours:24}")
    private int tokenExpiryHours;

    /**
     * 生成激活令牌
     *
     * @return UUID字符串
     */
    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * 计算令牌过期时间
     *
     * @return 过期时间
     */
    public LocalDateTime calculateExpiryTime() {
        return LocalDateTime.now().plusHours(tokenExpiryHours);
    }

    /**
     * 判断令牌是否已过期
     *
     * @param expiryTime 过期时间
     * @return true-已过期，false-未过期
     */
    public boolean isTokenExpired(LocalDateTime expiryTime) {
        return expiryTime == null || LocalDateTime.now().isAfter(expiryTime);
    }
}
