package com.sunyard.sunfintech.utils.converter.easy;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.sunyard.sunfintech.entity.Dict;
import com.sunyard.sunfintech.utils.RedisUtils;
import com.sunyard.sunfintech.utils.converter.easy.annotation.ExcelConverterByEnum;
import com.sunyard.sunfintech.utils.converter.easy.annotation.ExcelConverterByRedis;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @DESCRIPTION EasyExcel自定义转换器
 */
public class EasyExcelUtilsCustomConverter implements Converter<Object> {
    @Override
    public Class<Object> supportJavaTypeKey() {
        return Object.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Object convertToJavaData(CellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return handleCustomAnnotationToJavaData(contentProperty.getField(), cellData.getStringValue());
    }

    @Override
    public CellData<Object> convertToExcelData(Object value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return handleCustomAnnotationToExcelData(contentProperty.getField(), value);
    }

    protected Object handleCustomAnnotationToJavaData(Field field, String stringValue) {
        if (stringValue != null) {
            Object handleExcelConverterByEnumToJavaData = handleExcelConverterByEnumToJavaData(field, stringValue);
            // 要是为null 可能会处理其他的自定义注解 所以这里的if不简写
            if (handleExcelConverterByEnumToJavaData != null) {
                return handleExcelConverterByEnumToJavaData;
            }
        }
        return convertToJavaData(stringValue);
    }

    protected CellData<Object> handleCustomAnnotationToExcelData(Field field, Object value) {
        if (value != null) {
            CellData<Object> handleExcelConverterByEnum = handleExcelConverterByEnumToExcelData(field, value);
            if (handleExcelConverterByEnum != null) {
                return handleExcelConverterByEnum;
            }
        }
        return convertToExcelData(value);
    }

    protected Object handleExcelConverterByEnumToJavaData(Field field, String stringValue) {
        ExcelConverterByEnum excelConverterByEnum = field.getAnnotation(ExcelConverterByEnum.class);
        if (excelConverterByEnum != null) {
            try {
                Class<?> converter = excelConverterByEnum.converter();
                String convertToJavaDataMethod = excelConverterByEnum.convertToJavaDataMethod();
                Method method = converter.getMethod(convertToJavaDataMethod, String.class);
                String converterStr = (String) method.invoke(converter, stringValue);
                if (converterStr != null) {
                    return converterStr;
                }
                String defaultValue = excelConverterByEnum.convertToJavaDataDefaultValue();
                if (!"".equals(defaultValue)) {
                    return defaultValue;
                }
            } catch (Exception ignored) {
            }
        }
        ExcelConverterByRedis excelConverterByRedis = field.getAnnotation(ExcelConverterByRedis.class);
        if (excelConverterByRedis != null) {
            try {
                // keyIndex
                String keyIndex = excelConverterByRedis.value();
                Dict dictByValue = RedisUtils.getDictByValue(keyIndex, stringValue);
                if (dictByValue != null) {
                    return dictByValue.getCode();
                }
                String defaultValue = excelConverterByRedis.convertToJavaDataDefaultValue();
                if (!"".equals(defaultValue)) {
                    return defaultValue;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    protected CellData<Object> handleExcelConverterByEnumToExcelData(Field field, Object value) {
        ExcelConverterByEnum excelConverterByEnum = field.getAnnotation(ExcelConverterByEnum.class);
        if (excelConverterByEnum != null) {
            try {
                Class<?> converter = excelConverterByEnum.converter();
                String convertToExcelDataMethod = excelConverterByEnum.convertToExcelDataMethod();
                Method method = converter.getMethod(convertToExcelDataMethod, String.class);
                String converterStr = (String) method.invoke(converter, value.toString());
                if (converterStr != null) {
                    return new CellData<>(converterStr);
                }
                String defaultValue = excelConverterByEnum.convertToExcelDataDefaultValue();
                if (!"".equals(defaultValue)) {
                    return new CellData<>(defaultValue);
                }
            } catch (Exception ignored) {
            }
        }
        ExcelConverterByRedis excelConverterByRedis = field.getAnnotation(ExcelConverterByRedis.class);
        if (excelConverterByRedis != null) {
            try {
                // keyIndex
                String keyIndex = excelConverterByRedis.value();
                Dict dictByCode = RedisUtils.getDictByCode(keyIndex, value.toString());
                if (dictByCode != null) {
                    return new CellData<>(dictByCode.getValue());
                }
                String defaultValue = excelConverterByRedis.convertToExcelDataDefaultValue();
                if (!"".equals(defaultValue)) {
                    return new CellData<>(defaultValue);
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    /**
     * @DESCRIPTION 供继承类重写
     */
    protected Object convertToJavaData(String stringValue) {
        return stringValue;
    }

    /**
     * @DESCRIPTION 供继承类重写
     */
    protected CellData<Object> convertToExcelData(Object value) {
        if (value == null) {
            return new CellData<>("");
        }
        return new CellData<>(value);
    }

}
