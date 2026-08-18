package com.knowledge.base.common.utils;

import com.knowledge.base.common.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 *
 * <p>提供JWT Token的生成、解析和验证功能</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Component
public class JwtTokenUtil {

    @Resource
    private JwtConfig jwtConfig;

    /**
     * 生成访问Token
     *
     * @param userId 用户ID
     * @return Token
     */
    public String generateAccessToken(Long userId) {
        return generateToken(userId, null, null, jwtConfig.getExpiration() * 1000);
    }

    /**
     * 生成访问Token（带用户信息）
     *
     * @param userId 用户ID
     * @param username 用户名
     * @param avatar 头像URL
     * @return Token
     */
    public String generateAccessToken(Long userId, String username, String avatar) {
        return generateToken(userId, username, avatar, jwtConfig.getExpiration() * 1000);
    }

    /**
     * 生成访问Token（指定过期时间）
     *
     * @param userId 用户ID
     * @param username 用户名
     * @param avatar 头像URL
     * @param expirationSeconds 过期时间（秒）
     * @return Token
     */
    public String generateAccessToken(Long userId, String username, String avatar, Long expirationSeconds) {
        return generateToken(userId, username, avatar, expirationSeconds * 1000);
    }

    /**
     * 生成刷新Token
     *
     * @param userId 用户ID
     * @return Token
     */
    public String generateRefreshToken(Long userId) {
        return generateToken(userId, null, null, jwtConfig.getRefreshExpiration() * 1000);
    }

    /**
     * 生成Token
     *
     * @param userId 用户ID
     * @param expiration 过期时间（毫秒）
     * @return Token
     */
    private String generateToken(Long userId, String username, String avatar, Long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("type", "access");
        if (username != null) {
            claims.put("username", username);
        }
        if (avatar != null) {
            claims.put("avatar", avatar);
        }

        return Jwts.builder()
                .claims(claims)
                .subject(String.valueOf(userId))
                .issuer(jwtConfig.getIssuer())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(jwtConfig.secretKey())
                .compact();
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
                    .verifyWith(jwtConfig.secretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("解析Token失败：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 从Token中获取用户ID
     *
     * @param token Token
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return null;
        }
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 从Token中获取用户名
     *
     * @param token Token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return null;
        }
        Object username = claims.get("username");
        return username != null ? username.toString() : null;
    }

    /**
     * 从Token中获取头像URL
     *
     * @param token Token
     * @return 头像URL
     */
    public String getAvatarFromToken(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return null;
        }
        Object avatar = claims.get("avatar");
        return avatar != null ? avatar.toString() : null;
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
            if (claims == null) {
                return false;
            }
            Date expiration = claims.getExpiration();
            return expiration.after(new Date());
        } catch (Exception e) {
            log.error("验证Token失败：{}", e.getMessage());
            return false;
        }
    }

    /**
     * 检查Token是否即将过期
     *
     * @param token Token
     * @param thresholdSeconds 阈值（秒）
     * @return 是否即将过期
     */
    public boolean isTokenExpiringSoon(String token, int thresholdSeconds) {
        try {
            Claims claims = parseToken(token);
            if (claims == null) {
                return true;
            }
            Date expiration = claims.getExpiration();
            long timeToExpiry = expiration.getTime() - System.currentTimeMillis();
            return timeToExpiry < thresholdSeconds * 1000;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 刷新Token
     *
     * @param refreshToken 刷新Token
     * @return 新的访问Token
     */
    public String refreshToken(String refreshToken) {
        Claims claims = parseToken(refreshToken);
        if (claims == null) {
            throw new RuntimeException("刷新Token无效");
        }

        Long userId = Long.parseLong(claims.getSubject());
        return generateAccessToken(userId);
    }
}
