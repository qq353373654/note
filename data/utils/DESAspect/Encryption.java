package com.alipay.riskplus.dubbusiness.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description 字段加密和解密(放在要加解密的 实体类 和 String类型的字段和方法参数上)
 * @Author wuhh 2021-1-26
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER})
public @interface Encryption {
}
