package com.knowledge.base.common.enums;

import lombok.Getter;

/**
 * 文档状态枚举
 *
 * @author 苏三
 * @since 1.0.0
 */
@Getter
public enum DocumentStatus {

    /**
     * 草稿
     */
    DRAFT(0, "草稿"),

    /**
     * 已发布
     */
    PUBLISHED(1, "已发布"),

    /**
     * 已归档
     */
    ARCHIVED(2, "已归档"),

    /**
     * 待审核
     */
    PENDING_REVIEW(3, "待审核");

    private final Integer code;
    private final String name;

    DocumentStatus(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DocumentStatus getByCode(Integer code) {
        for (DocumentStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
