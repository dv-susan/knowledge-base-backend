package com.knowledge.base.common.annotation;

import java.lang.annotation.*;

/**
 * 限流注解
 *
 * @author 苏三
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * 限流key
     */
    String key() default "";

    /**
     * 时间窗口（秒）
     */
    int time() default 60;

    /**
     * 时间窗口内最大请求次数
     */
    int count() default 100;

    /**
     * 限流提示信息
     */
    String message() default "操作过于频繁，请稍后再试";
}
