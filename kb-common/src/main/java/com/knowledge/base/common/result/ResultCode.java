package com.knowledge.base.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应码枚举类
 *
 * <p>按照阿里巴巴Java开发规范设计，统一管理系统响应码</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 失败
     */
    ERROR(500, "操作失败"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),

    /**
     * 参数缺失
     */
    PARAM_MISSING(400, "参数缺失"),

    /**
     * 参数无效
     */
    PARAM_INVALID(400, "参数无效"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权，请先登录"),

    /**
     * 禁止访问
     */
    FORBIDDEN(403, "禁止访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 请求方法不支持
     */
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),

    /**
     * 请求超时
     */
    REQUEST_TIMEOUT(408, "请求超时"),

    /**
     * 系统内部错误
     */
    INTERNAL_SERVER_ERROR(500, "系统内部错误"),

    /**
     * 服务不可用
     */
    SERVICE_UNAVAILABLE(503, "服务不可用"),

    /**
     * 用户名或密码错误
     */
    USERNAME_OR_PASSWORD_ERROR(10001, "用户名或密码错误"),

    /**
     * 用户不存在
     */
    USER_NOT_EXIST(10002, "用户不存在"),

    /**
     * 用户已存在
     */
    USER_ALREADY_EXIST(10003, "用户已存在"),

    /**
     * 用户已被禁用
     */
    USER_DISABLED(10004, "用户未被激活"),

    /**
     * Token无效
     */
    TOKEN_INVALID(10005, "Token无效"),

    /**
     * Token已过期
     */
    TOKEN_EXPIRED(10006, "Token已过期"),

    /**
     * 无权限访问
     */
    ACCESS_DENIED(10007, "无权限访问"),

    /**
     * 文档不存在
     */
    DOCUMENT_NOT_EXIST(20001, "文档不存在"),

    /**
     * 文档已存在
     */
    DOCUMENT_ALREADY_EXIST(20002, "文档已存在"),

    /**
     * 文档分类不存在
     */
    CATEGORY_NOT_EXIST(20003, "文档分类不存在"),

    /**
     * 文件上传失败
     */
    FILE_UPLOAD_FAILED(20004, "文件上传失败"),

    /**
     * 文件类型不支持
     */
    FILE_TYPE_NOT_SUPPORTED(20005, "文件类型不支持"),

    /**
     * 文件大小超限
     */
    FILE_SIZE_EXCEEDED(20006, "文件大小超限");

    /**
     * 响应码
     */
    private final Integer code;

    /**
     * 响应消息
     */
    private final String message;

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
