package com.knowledge.base.common.utils;

import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * 密码工具类
 *
 * <p>提供密码加密和验证功能</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
public class PasswordUtil {

    /**
     * 加密密码
     *
     * @param password 明文密码
     * @return 加密后的密码
     */
    public static String encrypt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * 验证密码
     *
     * @param password       明文密码
     * @param hashedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean verify(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

    /**
     * 生成随机密码
     *
     * @param length 密码长度
     * @return 随机密码
     */
    public static String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            password.append(chars.charAt(index));
        }
        return password.toString();
    }

    /**
     * 检查密码强度
     *
     * @param password 密码
     * @return 强度等级（0-4）
     */
    public static int checkStrength(String password) {
        if (password == null || password.isEmpty()) {
            return 0;
        }

        int strength = 0;

        // 长度检查
        if (password.length() >= 8) {
            strength++;
        }
        if (password.length() >= 12) {
            strength++;
        }

        // 包含小写字母
        if (password.matches(".*[a-z].*")) {
            strength++;
        }

        // 包含大写字母
        if (password.matches(".*[A-Z].*")) {
            strength++;
        }

        // 包含数字
        if (password.matches(".*\\d.*")) {
            strength++;
        }

        // 包含特殊字符
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            strength++;
        }

        return Math.min(strength, 4);
    }

    /**
     * 获取密码强度描述
     *
     * @param strength 强度等级
     * @return 描述
     */
    public static String getStrengthDescription(int strength) {
        return switch (strength) {
            case 0 -> "非常弱";
            case 1 -> "弱";
            case 2 -> "一般";
            case 3 -> "强";
            case 4 -> "非常强";
            default -> "未知";
        };
    }
}
