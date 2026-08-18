package com.knowledge.base.userauth.service;

/**
 * 邮件发送服务接口
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface EmailService {

    /**
     * 发送账户激活邮件
     *
     * @param to       收件人邮箱
     * @param username 用户名
     * @param token    激活令牌
     */
    void sendActivationEmail(String to, String username, String token);

    /**
     * 发送密码重置验证码邮件
     *
     * @param to   收件人邮箱
     * @param code 6位验证码
     */
    void sendResetCodeEmail(String to, String code);
}
