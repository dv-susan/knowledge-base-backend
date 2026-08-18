package com.knowledge.base.common.annotation;

import java.lang.annotation.*;

/**
 * 权限验证注解
 *
 * @author 苏三
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {

    /**
     * 需要的权限
     */
    String value();

    /**
     * 权限描述
     */
    String description() default "";
}
