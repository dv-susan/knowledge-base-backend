package com.knowledge.base.document.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 标签类型枚举
 *
 * <p>按照阿里巴巴Java开发规范设计，定义标签类型</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum TagTypeEnum {

    /**
     * 系统标签
     */
    SYSTEM(0, "系统标签"),

    /**
     * 用户标签
     */
    USER(1, "用户标签");

    /**
     * 类型编码
     */
    private final Integer code;

    /**
     * 类型描述
     */
    private final String desc;

    /**
     * 根据编码获取枚举
     *
     * @param code 编码
     * @return 枚举值，找不到返回null
     */
    public static TagTypeEnum of(Integer code) {
        if (code == null) {
            return null;
        }
        for (TagTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 根据编码获取枚举，找不到返回默认值USER
     *
     * @param code 编码
     * @return 枚举值
     */
    public static TagTypeEnum ofOrDefault(Integer code) {
        TagTypeEnum type = of(code);
        return type != null ? type : USER;
    }
}
