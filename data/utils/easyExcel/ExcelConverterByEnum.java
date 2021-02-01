package com.sunyard.sunfintech.utils.converter.easy.annotation;

import java.lang.annotation.*;

/**
 * @DESCRIPTION 根据枚举,转换EasyExcel读取和输出的值
 * @Author wuhh
 * @Date 2020-8-7
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelConverterByEnum {

    /**
     * @DESCRIPTION 枚举的class
     */
    Class<? > converter() default Object.class;

    /**
     * @DESCRIPTION 从表格数据读取到JavaData用到的方法名称
     */
    String convertToJavaDataMethod() default "getCodeByMsg";

    /**
     * @DESCRIPTION 从实体数据转换到ExcelData用到的方法名称
     */
    String convertToExcelDataMethod() default "getMsgByCode";

    /**
     * @DESCRIPTION 从表格数据读取到JavaData失败的默认值
     */
    String convertToJavaDataDefaultValue() default "";

    /**
     * @DESCRIPTION 从实体数据转换到ExcelData失败的默认值
     */
    String convertToExcelDataDefaultValue() default "";

}
