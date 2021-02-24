package com.alipay.riskplus.dubbusiness.accounts.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface WebLogFlag {

    /** 请求方法自定义注释 */
    String methodNote() default "";
}
