package com.alipay.riskplus.dubbusiness.common.annotation;

import com.alipay.riskplus.dubbusiness.common.enums.DesensitizeTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Descrption 加在需要脱敏数据的方法和类和String字段和POJO字段上使返回的数据按 标记的类型脱敏,
 * 具体用到脱敏类型的地方是字段上的注解(类和方法上的脱敏类型没用到)
 * @Author wuhh 2021-2-8
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DesensitizeFlag {

    /** 脱敏类型 */
    DesensitizeTypeEnum desensitizeType() default DesensitizeTypeEnum.X;
    /** 执行方法时返回值脱敏 */
    boolean byReturn() default true;
    /** 用 fastJson 转换 Object 为 String 时,Object里的字段脱敏 */
    boolean byToJSONString() default true;
}
