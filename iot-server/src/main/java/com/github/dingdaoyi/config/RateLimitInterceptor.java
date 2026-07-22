package com.github.dingdaoyi.config;

import com.github.dingdaoyi.core.exception.BusinessException;
import com.github.dingdaoyi.core.enums.ResultCode;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-memory per-IP rate limiter using Caffeine token bucket.
 * ponytail: single-node, in-memory. For multi-node deployment, replace with Redis-backed bucket.
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Cache<String, AtomicInteger> buckets;

    public RateLimitInterceptor() {
        this.buckets = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(1))
                .maximumSize(10_000)
                .build();
    }

    @Override
    public boolean preHandle(jakarta.servlet.http.HttpServletRequest request,
                             jakarta.servlet.http.HttpServletResponse response,
                             Object handler) {
        String key = getClientIp(request);
        AtomicInteger counter = buckets.get(key, k -> new AtomicInteger(0));
        int current = counter.incrementAndGet();
        if (current > 120) {
            response.setStatus(429);
            throw new BusinessException(ResultCode.BAD_REQUEST, "请求过于频繁，请稍后再试");
        }
        return true;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
