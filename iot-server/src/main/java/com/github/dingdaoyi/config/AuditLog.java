package com.github.dingdaoyi.config;

import java.lang.annotation.*;

/**
 * 标记需要审计日志的 Controller 方法
 * @author dingyunwei
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditLog {
    String action();
    String resource() default "";
}
