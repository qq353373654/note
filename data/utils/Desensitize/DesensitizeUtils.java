package com.alipay.riskplus.dubbusiness.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.alipay.datasecurity.antmasking.commons.object.DesensConfig;
import com.alipay.datasecurity.antmasking.commons.object.DesensConfigBuilder;
import com.alipay.datasecurity.antmasking.desensitize.DesensException;
import com.alipay.datasecurity.antmasking.desensitize.DesensitizeUtil;
import com.alipay.riskplus.dubbusiness.common.annotation.DesensitizeFlag;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @Descrption 脱敏日志的工具类
 * @Author wuhh 2021-1-28
 */
@Slf4j
public class DesensitizeUtils {

    private static String appName;

    @Value("${spring.application.name}")
    public void setAppName(String appName) {
        DesensitizeUtils.appName = appName;
    }

    private static final DesensConfig desensConfig = new DesensConfigBuilder().build();

    /** 实例初始化时appName是null,这里get后才会有appName */
    public static DesensConfig getDesensConfig() {
        if (desensConfig.getAppCode() == null) {
            desensConfig.setAppCode(appName);
        }
        return desensConfig;
    }

    /**
     * @Descrption 数据脱敏
     */
    public static String desens(String typeToken, String content) {
        try {
            return DesensitizeUtil.desens(typeToken, content, getDesensConfig());
        } catch (DesensException e) {
            String warnMsg = "脱敏数据转换失败-typeToken:" + typeToken;
            log.warn(warnMsg, e);
            return "*";
        }
    }

    /** 转换JSONString的脱敏方法 */
    public static String toJSONString(Object object) {
        return JSON.toJSONString(object, getValueFilter());
    }

    private static final ValueFilter getValueFilter() {
        // obj-对象  key-字段名  value-字段值
        return (obj, key, value) -> {
            try {
                Field field = obj.getClass().getDeclaredField(key);
                DesensitizeFlag annotation = field.getAnnotation(DesensitizeFlag.class);
                if (null != annotation && annotation.byToJSONString()) {
                    if (value instanceof String) {
                        String strVal = (String) value;
                        if (StringUtils.isNotBlank(strVal)) {
                            return desens(annotation.desensitizeType().getType(), strVal);
                        }
                    } else {
                        analysisTypeByDesensitizeObject(value, false, true);
                    }
                }
            } catch (NoSuchFieldException e) {
                // 找不到的field对功能没有影响,空处理
            }
            return value;
        };
    }

    public static void analysisTypeByDesensitizeObject(Object data) {
        analysisTypeByDesensitizeObject(data, true, true);
    }

    @SuppressWarnings("unchecked")
    public static void analysisTypeByDesensitizeObject(Object data, boolean byReturn, boolean byToJSONString) {
        if (!(byReturn || byToJSONString)) {
            return;
        }
        if (data instanceof List) {
            List<Object> values = (List<Object>) data;
            for (Object object : values) {
                desensitizeObject(object, byReturn, byToJSONString);
            }
        } else if (data instanceof IPage) {
            IPage<Object> iPage = (IPage<Object>) data;
            List<Object> records = iPage.getRecords();
            for (Object object: records) {
                desensitizeObject(object, byReturn, byToJSONString);
            }
        } else {
            desensitizeObject(data, byReturn, byToJSONString);
        }
    }

    /**
     * 对实体内的字段进行脱敏
     */
    public static <T> void desensitizeObject(T t) {
        desensitizeObject(t, true, true);
    }

    /**
     * 对实体内的字段进行脱敏
     */
    public static <T> void desensitizeObject(T t, boolean byReturn, boolean byToJSONString) {
        if (null == t) {
            return;
        }
        DesensitizeFlag annotation = t.getClass().getAnnotation(DesensitizeFlag.class);
        if (null == annotation || !(byReturn && annotation.byReturn()) || !(byToJSONString && annotation.byToJSONString())) {
            return;
        }
        Field[] declaredFields = t.getClass().getDeclaredFields();
        try {
            if (declaredFields.length > 0) {
                for (Field field : declaredFields) {
                    DesensitizeFlag fieldAnnotation = field.getAnnotation(DesensitizeFlag.class);
                    if (fieldAnnotation != null) {
                        String fieldDesensitizeType = fieldAnnotation.desensitizeType().getType();
                        field.setAccessible(true);
                        if(field.getType().toString().endsWith("String")) {
                            String fieldValue = (String) field.get(t);
                            if (StringUtils.isNotEmpty(fieldValue)) {
                                field.set(t, DesensitizeUtils.desens(fieldDesensitizeType, fieldValue));
                            }
                        } else {
                            Object fieldValue = field.get(t);
                            analysisTypeByDesensitizeObject(fieldValue);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("DesensitizeUtils.desensitizeObject-error:", e);
        }
    }

}
