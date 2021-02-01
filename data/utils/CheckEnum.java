package com.sunyard.sunfintech.annotation;

import com.google.common.base.Strings;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @Description 校验枚举值有效性 https://www.cnblogs.com/lw5946/p/11574987.html
 * 每个约束都有参数 message，groups 和 payload。这是 Bean Validation 规范的要求
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckEnum.Validator.class)
public @interface CheckEnum {

    /** 在违反约束的情况下返回一个默认 key 以用于创建错误消息 */
    String message() default "枚举的有效性校验不通过";
    /** 允许指定此约束所属的验证分组。必须默认是一个空 Class 数组 */
    Class<?>[] groups() default {};
    /** 能被 Bean Validation API 客户端使用，以自定义一个注解的 payload 对象。API 本身不使用此属性。自定义 payload 可以是用来定义严重程度。 */
    Class<? extends Payload>[] payload() default {};

    /** 待校验的枚举类 */
    Class<? extends Enum<?>> enumClass();
    /** 校验使用的枚举方法名 */
    String checkMethod() default "isEnumByCode";
    /** 是否必填 */
    boolean required() default false;

    class Validator implements ConstraintValidator<CheckEnum, Object> {

        private Class<? extends Enum<?>> enumClass;
        private String checkMethod;
        private boolean required;

        @Override
        public void initialize(CheckEnum checkEnum) {
            this.enumClass = checkEnum.enumClass();
            this.checkMethod = checkEnum.checkMethod();
            this.required = checkEnum.required();
        }

        @Override
        public boolean isValid(Object value, ConstraintValidatorContext context) {
            // value为空时,必填返回false
            if (value == null) {
                return !required;
            }

            Class<?> valueClass = value.getClass();
            try {
                Method method = enumClass.getMethod(checkMethod, valueClass);
                if(!Modifier.isStatic(method.getModifiers())) {
                    throw new RuntimeException(String.format("%s method is not static method in the %s class", checkMethod, enumClass));
                }
                return (boolean) method.invoke(null, value);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException | SecurityException e) {
                throw new RuntimeException(String.format("This %s(%s) method does not exist in the %s", checkMethod, valueClass, enumClass), e);
            }
        }
    }
}
