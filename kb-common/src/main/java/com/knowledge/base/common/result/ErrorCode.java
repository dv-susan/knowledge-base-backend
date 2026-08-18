package com.knowledge.base.common.result;

import lombok.Getter;

/**
 * 错误码枚举
 *
 * @author 苏三
 * @since 1.0.0
 */
@Getter
public enum ErrorCode {

    /**
     * 通用错误码
     */
    SUCCESS(200, "操作成功"),
    ERROR(500, "系统错误"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),

    /**
     * 用户相关错误码 (1000-1999)
     */
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "密码错误"),
    USER_ACCOUNT_DISABLED(1003, "账户已被禁用"),
    USER_ALREADY_EXISTS(1004, "用户已存在"),
    USER_TOKEN_INVALID(1005, "Token无效"),
    USER_TOKEN_EXPIRED(1006, "Token已过期"),

    /**
     * 文档相关错误码 (2000-2999)
     */
    DOC_NOT_FOUND(2001, "文档不存在"),
    DOC_ALREADY_EXISTS(2002, "文档已存在"),
    DOC_VERSION_ERROR(2003, "文档版本错误"),
    DOC_STATUS_ERROR(2004, "文档状态错误"),
    DOC_PERMISSION_DENIED(2005, "无文档操作权限"),

    /**
     * 文件相关错误码 (3000-3999)
     */
    FILE_NOT_FOUND(3001, "文件不存在"),
    FILE_UPLOAD_ERROR(3002, "文件上传失败"),
    FILE_SIZE_EXCEED(3003, "文件大小超出限制"),
    FILE_TYPE_ERROR(3004, "文件类型不支持"),
    FILE_DOWNLOAD_ERROR(3005, "文件下载失败"),

    /**
     * 权限相关错误码 (4000-4999)
     */
    PERMISSION_NOT_FOUND(4001, "权限不存在"),
    PERMISSION_DENIED(4002, "权限不足"),
    ROLE_NOT_FOUND(4003, "角色不存在"),
    ROLE_ALREADY_EXISTS(4004, "角色已存在"),

    /**
     * 团队相关错误码 (5000-5999)
     */
    TEAM_NOT_FOUND(5001, "团队不存在"),
    TEAM_ALREADY_EXISTS(5002, "团队已存在"),
    TEAM_MEMBER_EXISTS(5003, "成员已存在"),
    TEAM_MEMBER_NOT_FOUND(5004, "成员不存在"),

    /**
     * 搜索相关错误码 (6000-6999)
     */
    SEARCH_ERROR(6001, "搜索失败"),
    SEARCH_INDEX_ERROR(6002, "索引创建失败"),

    /**
     * 业务相关错误码 (7000-7999)
     */
    BUSINESS_ERROR(7001, "业务处理失败"),
    DATA_CONFLICT(7002, "数据冲突"),
    OPERATION_TOO_FREQUENT(7003, "操作过于频繁");

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误信息
     */
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
