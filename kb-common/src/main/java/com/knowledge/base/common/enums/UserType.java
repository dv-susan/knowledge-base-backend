package com.knowledge.base.common.enums;

import lombok.Getter;

/**
 * 用户类型枚举
 *
 * @author 苏三
 * @since 1.0.0
 */
@Getter
public enum UserType {

    /**
     * 超级管理员
     */
    SUPER_ADMIN(0, "SUPER_ADMIN", "超级管理员"),

    /**
     * 知识管理员
     */
    KNOWLEDGE_ADMIN(1, "KNOWLEDGE_ADMIN", "知识管理员"),

    /**
     * 内容管理员
     */
    CONTENT_ADMIN(2, "CONTENT_ADMIN", "内容管理员"),

    /**
     * 团队负责人
     */
    TEAM_LEADER(3, "TEAM_LEADER", "团队负责人"),

    /**
     * 贡献者
     */
    CONTRIBUTOR(4, "CONTRIBUTOR", "内容贡献者"),

    /**
     * 普通用户
     */
    VIEWER(5, "VIEWER", "普通用户");

    private final Integer code;
    private final String roleCode;
    private final String name;

    UserType(Integer code, String roleCode, String name) {
        this.code = code;
        this.roleCode = roleCode;
        this.name = name;
    }

    public static UserType getByCode(Integer code) {
        for (UserType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    public static UserType getByRoleCode(String roleCode) {
        for (UserType type : values()) {
            if (type.getRoleCode().equals(roleCode)) {
                return type;
            }
        }
        return null;
    }
}
