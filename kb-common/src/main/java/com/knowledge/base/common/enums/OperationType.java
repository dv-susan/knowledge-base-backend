package com.knowledge.base.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作类型枚举
 *
 * @author 苏三
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum OperationType {

    /**
     * 查询操作
     */
    QUERY("查询"),

    /**
     * 新增操作
     */
    CREATE("新增"),

    /**
     * 更新操作
     */
    UPDATE("更新"),

    /**
     * 删除操作
     */
    DELETE("删除"),

    /**
     * 导出操作
     */
    EXPORT("导出"),

    /**
     * 导入操作
     */
    IMPORT("导入"),

    /**
     * 登录操作
     */
    LOGIN("登录"),

    /**
     * 登出操作
     */
    LOGOUT("登出"),

    /**
     * 审核操作
     */
    REVIEW("审核"),

    /**
     * 其他操作
     */
    OTHER("其他");

    /**
     * 操作类型描述
     */
    private final String description;

    /**
     * 根据描述获取操作类型
     *
     * @param description 操作描述
     * @return 操作类型
     */
    public static OperationType fromDescription(String description) {
        for (OperationType type : values()) {
            if (type.getDescription().equals(description)) {
                return type;
            }
        }
        return OTHER;
    }
}
