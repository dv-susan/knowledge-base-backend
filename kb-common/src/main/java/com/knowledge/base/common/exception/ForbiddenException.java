package com.knowledge.base.common.exception;

import com.knowledge.base.common.result.ResultCode;

/**
 * 禁止访问异常类
 *
 * <p>用于处理用户无权限访问资源的情况</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
public class ForbiddenException extends BusinessException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造方法
     */
    public ForbiddenException() {
        super(ResultCode.FORBIDDEN);
    }

    /**
     * 构造方法
     *
     * @param message 错误消息
     */
    public ForbiddenException(String message) {
        super(ResultCode.FORBIDDEN.getCode(), message);
    }

    /**
     * 构造方法
     *
     * @param message 错误消息
     * @param cause   原因
     */
    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
