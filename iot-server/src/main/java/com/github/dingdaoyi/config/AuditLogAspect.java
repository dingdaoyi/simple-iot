package com.github.dingdaoyi.config;

import cn.dev33.satoken.stp.StpUtil;
import com.github.dingdaoyi.mapper.AuditLogMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 审计日志AOP切面，拦截 @AuditLog 注解的方法
 * ponytail: sync write, async queue if throughput becomes an issue
 */
@Aspect
@Component
@Slf4j
public class AuditLogAspect {

    @Resource
    private AuditLogMapper auditLogMapper;

    @Around("@annotation(auditLog)")
    public Object around(ProceedingJoinPoint joinPoint, AuditLog auditLog) throws Throwable {
        Object result = joinPoint.proceed();
        try {
            com.github.dingdaoyi.entity.AuditLog entity = new com.github.dingdaoyi.entity.AuditLog();
            entity.setAction(auditLog.action());
            entity.setResource(auditLog.resource());

            // extract resource ID from path variable if first arg is Integer/String
            Object[] args = joinPoint.getArgs();
            if (args.length > 0 && args[0] != null) {
                entity.setResourceId(String.valueOf(args[0]));
            }

            // username from Sa-Token
            try {
                entity.setUsername((String) StpUtil.getLoginId());
            } catch (Exception ignored) {
                entity.setUsername("anonymous");
            }

            // client IP
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty()) ip = request.getRemoteAddr();
                entity.setIp(ip);
                if (entity.getResource().isEmpty()) {
                    entity.setResource(request.getMethod() + " " + request.getRequestURI());
                }
            }

            auditLogMapper.insert(entity);
        } catch (Exception e) {
            log.warn("审计日志写入失败: {}", e.getMessage());
        }
        return result;
    }
}
