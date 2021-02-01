package com.sunyard.sunfintech.utils.converter.easy.annotation;

import java.lang.annotation.*;

/**
 * @DESCRIPTION 根据Redis的字典数据缓存,转换EasyExcel读取和输出的值
 * @Author wuhh
 * @Date 2020-8-7
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelConverterByRedis {

    /**
     * @DESCRIPTION keyIndex
     */
    String value() default "";

    /**
     * @DESCRIPTION 从表格数据读取到JavaData失败的默认值
     */
    String convertToJavaDataDefaultValue() default "";

    /**
     * @DESCRIPTION 从实体数据转换到ExcelData失败的默认值
     */
    String convertToExcelDataDefaultValue() default "";

}
