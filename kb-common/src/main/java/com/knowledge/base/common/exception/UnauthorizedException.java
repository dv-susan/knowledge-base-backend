package com.knowledge.base.common.exception;

import com.knowledge.base.common.result.ResultCode;

/**
 * 未授权异常类
 *
 * <p>用于处理用户未登录或Token无效的情况</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
public class UnauthorizedException extends BusinessException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造方法
     */
    public UnauthorizedException() {
        super(ResultCode.UNAUTHORIZED);
    }

    /**
     * 构造方法
     *
     * @param message 错误消息
     */
    public UnauthorizedException(String message) {
        super(ResultCode.UNAUTHORIZED.getCode(), message);
    }

    /**
     * 构造方法
     *
     * @param message 错误消息
     * @param cause   原因
     */
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
