package com.kk.common.annotation;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;

import java.lang.annotation.*;

/**
 * created with IntelliJ IDEA.
 * description: 基础事务注解
 * auth: kk
 * date: 2026-05-29
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BaseTransactional {
    int timeout() default 60;

    boolean readOnly() default false;

    Propagation propagation() default Propagation.REQUIRED;

    Isolation isolation() default Isolation.DEFAULT;
}
