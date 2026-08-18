package com.knowledge.base.userauth.util;

import cn.hutool.crypto.digest.BCrypt;

/**
 * 密码生成工具
 * 用于生成 BCrypt 加密的密码
 */
public class PasswordGenerator {

    public static void main(String[] args) {
        // 生成密码 123456 的 BCrypt 哈希值
        String password = "123456";
        String hashed = BCrypt.hashpw(password);

        System.out.println("========================================");
        System.out.println("密码: " + password);
        System.out.println("BCrypt 哈希值: " + hashed);
        System.out.println("========================================");
        System.out.println();
        System.out.println("SQL 更新语句:");
        System.out.println("UPDATE kb_user.user SET password = '" + hashed + "' WHERE username = 'admin';");
        System.out.println();
        System.out.println("验证密码是否匹配:");
        System.out.println("验证结果: " + BCrypt.checkpw(password, hashed));
    }
}
