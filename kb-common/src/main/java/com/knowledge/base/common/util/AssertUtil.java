package com.knowledge.base.common.util;

import com.knowledge.base.common.exception.BusinessException;
import com.knowledge.base.common.result.ResultCode;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * 断言工具类
 *
 * <p>参考susan-mall-cloud的AssertUtil实现</p>
 * <p>提供业务参数校验，校验失败时抛出BusinessException</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
public class AssertUtil {

    /**
     * 断言表达式为true
     *
     * @param expression 表达式
     * @param message    错误消息
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), message);
        }
    }

    /**
     * 断言表达式为false
     *
     * @param expression 表达式
     * @param message    错误消息
     */
    public static void isFalse(boolean expression, String message) {
        if (expression) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), message);
        }
    }

    /**
     * 断言对象为null
     *
     * @param object  对象
     * @param message 错误消息
     */
    public static void isNull(Object object, String message) {
        if (Objects.nonNull(object)) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), message);
        }
    }

    /**
     * 断言对象不为null
     *
     * @param object  对象
     * @param message 错误消息
     */
    public static void notNull(Object object, String message) {
        if (Objects.isNull(object)) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), message);
        }
    }

    /**
     * 断言字符串有长度
     *
     * @param text    字符串
     * @param message 错误消息
     */
    public static void hasLength(String text, String message) {
        if (StringUtils.isEmpty(text)) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), message);
        }
    }

    /**
     * 断言字符串有内容（非空字符串）
     *
     * @param text    字符串
     * @param message 错误消息
     */
    public static void hasText(String text, String message) {
        if (!StringUtils.hasText(text)) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), message);
        }
    }

    /**
     * 断言集合不为空
     *
     * @param collection 集合
     * @param message    错误消息
     */
    public static void notEmpty(Collection<?> collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), message);
        }
    }

    /**
     * 断言Map不为空
     *
     * @param map     Map
     * @param message 错误消息
     */
    public static void notEmpty(Map<?, ?> map, String message) {
        if (map == null || map.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), message);
        }
    }

    /**
     * 断言数组有内容
     *
     * @param array   数组
     * @param message 错误消息
     */
    public static void notEmpty(Object[] array, String message) {
        if (array == null || array.length == 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), message);
        }
    }

    /**
     * 断言状态（通用断言）
     *
     * @param state   状态
     * @param message 错误消息
     */
    public static void state(boolean state, String message) {
        if (!state) {
            throw new BusinessException(message);
        }
    }
}
