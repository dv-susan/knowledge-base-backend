package com.knowledge.base.userauth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * JWT配置类
 *
 * <p>配置JWT Token生成和验证</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Configuration("userAuthJwtConfig")
public class JwtConfig {

    @Value("${jwt.secret:knowledge-base-secret-key-2024}")
    private String secret;

    @Value("${jwt.expiration:7200}")
    private Long expiration;

    @Value("${jwt.refresh-expiration:604800}")
    private Long refreshExpiration;

    @Value("${jwt.issuer:knowledge-base}")
    private String issuer;

    /**
     * 获取密钥
     *
     * @return SecretKey
     */
    @Bean
    public SecretKey secretKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        // 确保密钥至少256位
        if (keyBytes.length < 32) {
            // 如果密钥不足，使用Base64编码扩展
            keyBytes = Base64.getEncoder().encode(secret.getBytes(StandardCharsets.UTF_8));
        }
        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    /**
     * 获取Token过期时间
     *
     * @return 过期时间（秒）
     */
    public Long getExpiration() {
        return expiration;
    }

    /**
     * 获取刷新Token过期时间
     *
     * @return 过期时间（秒）
     */
    public Long getRefreshExpiration() {
        return refreshExpiration;
    }

    /**
     * 获取签发者
     *
     * @return 签发者
     */
    public String getIssuer() {
        return issuer;
    }
}
