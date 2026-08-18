package com.knowledge.base.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT工具类
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Component
public class JwtUtil {

    /**
     * JWT密钥
     */
    @Value("${jwt.secret:knowledge-base-secret-key-2024}")
    private String secret;

    /**
     * Token有效期（毫秒）
     */
    @Value("${jwt.expiration:7200000}")
    private Long expiration;

    /**
     * 生成Token
     *
     * @param subject    主题（通常是用户ID）
     * @param claims     自定义声明
     * @return Token
     */
    public String generateToken(String subject, Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * 生成Token（无自定义声明）
     *
     * @param subject 主题（通常是用户ID）
     * @return Token
     */
    public String generateToken(String subject) {
        return generateToken(subject, null);
    }

    /**
     * 解析Token
     *
     * @param token Token
     * @return Claims
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("解析Token失败: {}", e.getMessage());
            throw new RuntimeException("Token解析失败");
        }
    }

    /**
     * 从Token中获取主题
     *
     * @param token Token
     * @return 主题
     */
    public String getSubject(String token) {
        return parseToken(token).getSubject();
    }

    /**
     * 从Token中获取用户ID
     *
     * @param token Token
     * @return 用户ID
     */
    public Long getUserId(String token) {
        String subject = getSubject(token);
        try {
            return Long.parseLong(subject);
        } catch (NumberFormatException e) {
            log.error("解析用户ID失败: {}", subject);
            return null;
        }
    }

    /**
     * 验证Token是否有效
     *
     * @param token Token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            Date expiration = claims.getExpiration();
            return !expiration.before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查Token是否即将过期（剩余时间小于30分钟）
     *
     * @param token Token
     * @return 是否即将过期
     */
    public boolean isTokenExpiringSoon(String token) {
        try {
            Claims claims = parseToken(token);
            Date expiration = claims.getExpiration();
            long timeLeft = expiration.getTime() - System.currentTimeMillis();
            return timeLeft < (30 * 60 * 1000);
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 刷新Token
     *
     * @param token Token
     * @return 新Token
     */
    public String refreshToken(String token) {
        try {
            Claims claims = parseToken(token);
            String subject = claims.getSubject();
            return generateToken(subject, claims);
        } catch (Exception e) {
            log.error("刷新Token失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取密钥
     */
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
