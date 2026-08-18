package com.knowledge.base.userauth.service;

/**
 * 安全配置服务接口
 *
 * <p>从 kb_system_config 表读取安全相关配置，供登录、注册等流程使用</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface SecurityConfigService {

    /**
     * 获取密码最小长度
     */
    int getPasswordMinLength();

    /**
     * 是否需要包含特殊字符
     */
    boolean isRequireSpecialChar();

    /**
     * 获取密码策略等级（low/medium/high）
     */
    String getPasswordPolicy();

    /**
     * 获取会话超时时间（秒）
     */
    long getSessionTimeout();

    /**
     * 获取最大登录尝试次数
     */
    int getLoginMaxRetry();

    /**
     * 是否启用IP限制
     */
    boolean isIpRestrictionEnabled();

    /**
     * 是否启用双因素认证
     */
    boolean is2FAEnabled();

    /**
     * 获取指定配置项的值
     *
     * @param configKey 配置键
     * @return 配置值，不存在时返回 null
     */
    String getConfig(String configKey);

    /**
     * 验证密码是否符合策略
     *
     * @param password 明文密码
     * @throws com.knowledge.base.common.exception.BusinessException 密码不符合策略时抛出
     */
    void validatePassword(String password);

    /**
     * 记录登录失败
     *
     * @param username 用户名
     * @return 剩余尝试次数（0表示已锁定）
     */
    int recordLoginFailure(String username);

    /**
     * 清除登录失败记录
     *
     * @param username 用户名
     */
    void clearLoginFailure(String username);

    /**
     * 检查账号是否被锁定
     *
     * @param username 用户名
     * @return true表示已锁定
     */
    boolean isAccountLocked(String username);
}
