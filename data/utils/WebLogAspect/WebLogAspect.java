package com.alipay.riskplus.dubbusiness.accounts.aop;

import com.alipay.riskplus.dubbusiness.accounts.annotation.WebLogFlag;
import com.alipay.riskplus.dubbusiness.accounts.entity.WebLog;
import com.alipay.riskplus.dubbusiness.accounts.mapper.WebLogMapper;
import com.alipay.riskplus.dubbusiness.common.entity.user.SysUser;
import com.alipay.riskplus.dubbusiness.common.exception.BaseException;
import com.alipay.riskplus.dubbusiness.common.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @Descrption web日志记录切面
 * 敏感操作审计日志留存通用方法,control进入后要把操作用户的信息存入表中:
 * 操作人,方法名(类.方法),方法名(自定义备注),操作时间,请求ip,和一些常见的属性
 * @Author wuhh 2021-2-3
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class WebLogAspect {

    private final WebLogMapper webLogMapper;

    @Before(value = "getMethods()")
    public void doAroundLog(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取操作人
        SysUser user = AuthUtils.getUser();
        String userNo = null;
        String userName = null;
        if (user != null) {
            userNo = user.getUid();
            userName = user.getNickname();
        }
        // 获取类名和方法名
        Method method = signature.getMethod();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = method.getName();
        // 获取方法名(自定义备注)
        WebLogFlag webLogFlag = method.getAnnotation(WebLogFlag.class);
        String methodNote = webLogFlag.methodNote();
        // 获取操作时间
        LocalDateTime requestTime = LocalDateTime.now();
        // 获取请求ip
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String requestIp = getRemoteIP(request);
        log.info("userNo:{},userName:{},className:{},methodName:{},methodNote:{},requestTime:{},requestIp:{}", userNo, userName, className, methodName, methodNote, requestTime, requestIp);
        WebLog webLog = WebLog.builder()
                .className(className)
                .methodName(methodName)
                .methodNote(methodNote)
                .userNo(userNo)
                .userName(userName)
                .requestTime(requestTime)
                .requestIp(requestIp)
                .build();
        int insert = webLogMapper.insert(webLog);
        if (insert < 1) {
            log.error("web日志记录,保存失败-className:{},methodName:{}", className, methodName);
            throw new BaseException("web日志记录,保存失败.");
        }
    }

    @Pointcut("@annotation(com.alipay.riskplus.dubbusiness.accounts.annotation.WebLogFlag)")
    public void getMethods() {
    }

    private String getRemoteIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null) {
            //对于通过多个代理的情况，最后IP为客户端真实IP,多个IP按照','分割
            int position = ip.indexOf(",");
            if (position > 0) {
                ip = ip.substring(0, position);
            }
        }
        return ip;
    }

}