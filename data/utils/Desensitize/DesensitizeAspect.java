package com.alipay.riskplus.dubbusiness.accounts.aop;

import com.alipay.riskplus.dubbusiness.common.annotation.DesensitizeFlag;
import com.alipay.riskplus.dubbusiness.common.entity.CommonResponse;
import com.alipay.riskplus.dubbusiness.common.utils.DesensitizeUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @Descrption 数据脱敏,执行方法后把返回值脱敏
 * @Author wuhh 2021-2-8
 */
@Aspect
@Component
@Slf4j
public class DesensitizeAspect {

    @SuppressWarnings("unchecked")
    @Around(value = "getMethods()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法参数
        Object[] args = joinPoint.getArgs();
        // 执行方法
        Object proceed = joinPoint.proceed(args);
        // 脱敏
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        DesensitizeFlag desensitizeFlag = method.getAnnotation(DesensitizeFlag.class);
        String defaultDesensitizeType = desensitizeFlag.desensitizeType().getType();
        if (desensitizeFlag.byReturn()) {
            if (proceed instanceof String) {
                return DesensitizeUtils.desens(defaultDesensitizeType, (String) proceed);
            } else if (proceed instanceof CommonResponse) {
                CommonResponse<Object> commonResponse = ((CommonResponse<Object>) proceed);
                Object obj = commonResponse.getData();
                // 脱敏
                if (obj instanceof String) {
                    commonResponse.setData(DesensitizeUtils.desens(defaultDesensitizeType, (String) obj));
                } else {
                    DesensitizeUtils.analysisTypeByDesensitizeObject(obj, true, false);
                }
            } else {
                DesensitizeUtils.analysisTypeByDesensitizeObject(proceed, true, false);
            }
        }
        return proceed;
    }

    @Pointcut("@annotation(com.alipay.riskplus.dubbusiness.common.annotation.DesensitizeFlag)")
    public void getMethods() {
    }
}
