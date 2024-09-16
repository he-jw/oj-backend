package com.jingweihe.oj.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验
 *
 *
 *
 */
@Target(ElementType.METHOD) // 该注解将应用于方法
@Retention(RetentionPolicy.RUNTIME) // 该注解在运行时保留
public @interface AuthCheck {

    /**
     * 必须有某个角色
     *
     * @return
     */
    String mustRole() default "";

}

