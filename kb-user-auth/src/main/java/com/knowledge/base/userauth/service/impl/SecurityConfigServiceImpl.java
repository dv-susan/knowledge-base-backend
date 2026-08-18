package com.knowledge.base.userauth.service.impl;

import com.knowledge.base.common.config.SystemConfigCache;
import com.knowledge.base.common.exception.BusinessException;
import com.knowledge.base.userauth.service.SecurityConfigService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 安全配置服务实现
 *
 * <p>从 Redis 缓存读取系统配置，并提供密码验证和登录失败计数功能</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
public class SecurityConfigServiceImpl implements SecurityConfigService {

    @Resource
    private SystemConfigCache systemConfigCache;

    /** 登录失败记录 */
    private final ConcurrentHashMap<String, LoginFailRecord> failMap = new ConcurrentHashMap<>();

    private static final int DEFAULT_PASSWORD_MIN_LENGTH = 8;
    private static final long DEFAULT_SESSION_TIMEOUT = 3600L;
    private static final int DEFAULT_LOGIN_MAX_RETRY = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 15;

    // ==================== 配置读取 ====================

    @Override
    public int getPasswordMinLength() {
        String val = getConfig("auth.password.min.length");
        if (val != null) {
            try {
                return Integer.parseInt(val.trim());
            } catch (NumberFormatException ignored) {
            }
        }
        return DEFAULT_PASSWORD_MIN_LENGTH;
    }

    @Override
    public boolean isRequireSpecialChar() {
        String val = getConfig("auth.password.require.special");
        return "true".equalsIgnoreCase(val) || "1".equals(val);
    }

    @Override
    public String getPasswordPolicy() {
        String val = getConfig("system.passwordPolicy");
        return (val != null && !val.isEmpty()) ? val : "medium";
    }

    @Override
    public long getSessionTimeout() {
        String val = getConfig("auth.session.timeout");
        if (val != null) {
            try {
                return Long.parseLong(val.trim());
            } catch (NumberFormatException ignored) {
            }
        }
        return DEFAULT_SESSION_TIMEOUT;
    }

    @Override
    public int getLoginMaxRetry() {
        String val = getConfig("auth.login.max.retry");
        if (val != null) {
            try {
                return Integer.parseInt(val.trim());
            } catch (NumberFormatException ignored) {
            }
        }
        return DEFAULT_LOGIN_MAX_RETRY;
    }

    @Override
    public boolean isIpRestrictionEnabled() {
        String val = getConfig("system.ipRestriction");
        return "true".equalsIgnoreCase(val) || "1".equals(val);
    }

    @Override
    public boolean is2FAEnabled() {
        String val = getConfig("system.enable2FA");
        return "true".equalsIgnoreCase(val) || "1".equals(val);
    }

    @Override
    public String getConfig(String configKey) {
        return systemConfigCache.getConfig(configKey);
    }

    // ==================== 密码策略验证 ====================

    @Override
    public void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new BusinessException("密码不能为空");
        }

        int minLength = getPasswordMinLength();
        boolean requireSpecial = isRequireSpecialChar();
        String policy = getPasswordPolicy();

        // 最小长度检查
        if (password.length() < minLength) {
            throw new BusinessException("密码长度不能少于" + minLength + "位");
        }

        switch (policy) {
            case "low" -> validateLowPolicy(password, requireSpecial);
            case "medium" -> validateMediumPolicy(password, requireSpecial);
            case "high" -> validateHighPolicy(password, requireSpecial);
            default -> validateMediumPolicy(password, requireSpecial);
        }
    }

    private void validateLowPolicy(String password, boolean requireSpecial) {
        // low: 仅检查最小长度和特殊字符要求
        if (requireSpecial && !containsSpecialChar(password)) {
            throw new BusinessException("密码必须包含至少一个特殊字符（如 !@#$%^&*）");
        }
    }

    private void validateMediumPolicy(String password, boolean requireSpecial) {
        // medium: 至少包含字母和数字
        if (!containsLetter(password) || !containsDigit(password)) {
            throw new BusinessException("密码必须同时包含字母和数字");
        }
        if (requireSpecial && !containsSpecialChar(password)) {
            throw new BusinessException("密码必须包含至少一个特殊字符（如 !@#$%^&*）");
        }
    }

    private void validateHighPolicy(String password, boolean requireSpecial) {
        // high: 必须包含大写字母、小写字母、数字和特殊字符
        if (!containsUpperCase(password)) {
            throw new BusinessException("密码必须包含至少一个大写字母");
        }
        if (!containsLowerCase(password)) {
            throw new BusinessException("密码必须包含至少一个小写字母");
        }
        if (!containsDigit(password)) {
            throw new BusinessException("密码必须包含至少一个数字");
        }
        if (!containsSpecialChar(password)) {
            throw new BusinessException("密码必须包含至少一个特殊字符（如 !@#$%^&*）");
        }
    }

    private boolean containsDigit(String str) {
        return str.chars().anyMatch(Character::isDigit);
    }

    private boolean containsLetter(String str) {
        return str.chars().anyMatch(Character::isLetter);
    }

    private boolean containsUpperCase(String str) {
        return str.chars().anyMatch(c -> Character.isUpperCase(c));
    }

    private boolean containsLowerCase(String str) {
        return str.chars().anyMatch(c -> Character.isLowerCase(c));
    }

    private boolean containsSpecialChar(String str) {
        return str.chars().anyMatch(c -> !Character.isLetterOrDigit(c));
    }

    // ==================== 登录失败计数 ====================

    @Override
    public int recordLoginFailure(String username) {
        int maxRetry = getLoginMaxRetry();
        LoginFailRecord record = failMap.compute(username, (k, v) -> {
            if (v == null || v.isExpired()) {
                return new LoginFailRecord(1, System.currentTimeMillis());
            }
            v.attempts++;
            v.lastAttemptTime = System.currentTimeMillis();
            return v;
        });

        int remaining = maxRetry - record.attempts;
        log.info("登录失败记录：username={}, attempts={}/{}, remaining={}",
                username, record.attempts, maxRetry, Math.max(0, remaining));
        return Math.max(0, remaining);
    }

    @Override
    public void clearLoginFailure(String username) {
        failMap.remove(username);
    }

    @Override
    public boolean isAccountLocked(String username) {
        LoginFailRecord record = failMap.get(username);
        if (record == null) {
            return false;
        }
        int maxRetry = getLoginMaxRetry();
        if (record.attempts < maxRetry) {
            return false;
        }
        // 检查锁定时间是否已过
        long lockDurationMs = LOCKOUT_DURATION_MINUTES * 60L * 1000L;
        if (System.currentTimeMillis() - record.lastAttemptTime > lockDurationMs) {
            failMap.remove(username);
            return false;
        }
        return true;
    }

    // ==================== 定时清理 ====================

    /**
     * 每小时清理过期的登录失败记录
     */
    @Scheduled(fixedRate = 3600000)
    public void cleanExpiredFailRecords() {
        long expireTime = System.currentTimeMillis() - LOCKOUT_DURATION_MINUTES * 60L * 1000L;
        failMap.entrySet().removeIf(entry -> entry.getValue().lastAttemptTime < expireTime);
    }

    // ==================== 内部类 ====================

    private static class LoginFailRecord {
        int attempts;
        long lastAttemptTime;

        LoginFailRecord(int attempts, long lastAttemptTime) {
            this.attempts = attempts;
            this.lastAttemptTime = lastAttemptTime;
        }

        boolean isExpired() {
            return System.currentTimeMillis() - lastAttemptTime > LOCKOUT_DURATION_MINUTES * 60L * 1000L;
        }
    }
}
