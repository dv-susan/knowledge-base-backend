package com.knowledge.base.document.utils;

import com.knowledge.base.common.utils.UserContextUtil;

/**
 * 用户上下文工具类
 *
 * <p>从ThreadLocal中获取当前登录用户信息</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
public class UserContext {

    /**
     * 获取当前用户ID
     *
     * @return 用户ID
     * @throws IllegalStateException 如果用户未登录（生产环境应抛出异常）
     */
    public static Long getCurrentUserId() {
        Long userId = UserContextUtil.getUserId();
        if (userId == null) {
            // 如果从上下文获取不到，抛出异常（不应该在已登录状态下发生）
            throw new IllegalStateException("用户未登录或会话已过期，请重新登录");
        }
        return userId;
    }

    /**
     * 获取当前用户名
     *
     * @return 用户名
     */
    public static String getCurrentUserName() {
        String username = UserContextUtil.getUsername();
        if (username == null) {
            throw new IllegalStateException("用户登录信息不完整，请重新登录");
        }
        return username;
    }

    /**
     * 获取当前用户Token
     *
     * @return Token
     */
    public static String getCurrentToken() {
        return UserContextUtil.getToken();
    }

    /**
     * 获取当前用户头像
     *
     * @return 头像URL
     */
    public static String getCurrentUserAvatar() {
        return UserContextUtil.getAvatar();
    }
}
