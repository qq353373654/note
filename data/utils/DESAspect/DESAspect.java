package com.alipay.riskplus.dubbusiness.common.configuration;

import com.alipay.riskplus.dubbusiness.common.annotation.Encryption;
import com.alipay.riskplus.dubbusiness.common.utils.AESUtil;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

/**
 * @Description data encryption standard 数据出入库时加解密切面
 * @Author wuhh 2021-1-26
 */
@Aspect
@Component
@Slf4j
public class DESAspect {

    public final static String KEY = "H&uTuS&$!k";

    /**
     * Dao aspect.
     */
    @Pointcut("execution( * com.alipay.riskplus.dubbusiness.*.mapper..*.*(..)) " +
            "|| execution( * com.alipay.riskplus.dubbusiness.*.*.mapper..*.*(..))" +
            "|| execution( * com.baomidou.mybatisplus.extension.service.IService.saveBatch(..)) " +
            "|| execution( * com.baomidou.mybatisplus.extension.service.IService.saveOrUpdateBatch(..)) " +
            "|| execution( * com.baomidou.mybatisplus.extension.service.IService.updateBatchById(..)) ")
    public void daoAspect() {
    }

    /**
     * data encryption standard
     */
    @SuppressWarnings("unchecked")
    @Around("daoAspect()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法参数
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            Object param = args[i];
            if (param instanceof String) {
                Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
                // 获取dao层接口参数注解 start
                Annotation[][] parameterAnnotations = method.getParameterAnnotations();
                for (Annotation annotation : parameterAnnotations[i]) {
                    // 方法参数包含注解,进行加密
                    if (annotation instanceof Encryption) {
                        args[i] = AESUtil.encrypt((String) param, KEY);
                    }
                }
            } else if (param instanceof AbstractWrapper) {
                AbstractWrapper<Object, ?, ?> wrapper = (AbstractWrapper<Object, ?, ?>) param;
                Object object = wrapper.getEntity();
                encryptObject(object);
            } else if (param instanceof Collection) {
                Collection<Object> values = (Collection<Object>) param;
                for (Object object : values) {
                    encryptObject(object);
                }
            } else {
                encryptObject(param);
            }
        }

        // 执行方法
        Object proceed = joinPoint.proceed(args);
        // 解密
        if (proceed instanceof Collection) {
            Collection<Object> values = (Collection<Object>) proceed;
            for (Object object : values) {
                decryptObject(object);
            }
        } else if (proceed instanceof IPage) {
            IPage<Object> iPage = (IPage<Object>) proceed;
            List<Object> records = iPage.getRecords();
            for (Object object: records) {
                decryptObject(object);
            }
        } else {
            decryptObject(proceed);
        }
        return proceed;
    }


    /**
     * 对实体内的字段进行加密
     */
    private <T> void encryptObject(T t) {
        if (null == t) {
            return;
        }
        Encryption annotation = t.getClass().getAnnotation(Encryption.class);
        if (null == annotation) {
            return;
        }
        Field[] declaredFields = t.getClass().getDeclaredFields();
        try {
            if (declaredFields.length > 0) {
                for (Field field : declaredFields) {
                    if (field.isAnnotationPresent(Encryption.class) && field.getType().toString().endsWith("String")) {
                        field.setAccessible(true);
                        String fieldValue = (String) field.get(t);
                        if (StringUtils.isNotEmpty(fieldValue)) {
                            String encrypt = AESUtil.encrypt(fieldValue, KEY);
                            if (encrypt != null) {
                                field.set(t, encrypt);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("DESAspect.encryptObject-error:", e);
        }
    }

    /**
     * 对实体内的字段进行解密
     */
    private <T> void decryptObject(T t) {
        if (null == t) {
            return;
        }
        Encryption annotation = t.getClass().getAnnotation(Encryption.class);
        if (null == annotation) {
            return;
        }
        Field[] declaredFields = t.getClass().getDeclaredFields();
        try {
            if (declaredFields.length > 0) {
                for (Field field : declaredFields) {
                    if (field.isAnnotationPresent(Encryption.class) && field.getType().toString().endsWith("String")) {
                        field.setAccessible(true);
                        String fieldValue = (String) field.get(t);
                        if (StringUtils.isNotEmpty(fieldValue)) {
                            String decrypt = AESUtil.decrypt(fieldValue, KEY);
                            if (StringUtils.isNotBlank(decrypt)) {
                                field.set(t, decrypt);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("DESAspect.decryptObject-error:", e);
        }
    }
}
