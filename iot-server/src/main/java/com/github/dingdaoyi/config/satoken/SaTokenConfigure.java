package com.github.dingdaoyi.config.satoken;

import cn.dev33.satoken.interceptor.SaInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dingyunwei
 */
@Configuration
@Import(SsoProperties.class)
public class SaTokenConfigure implements WebMvcConfigurer {


    private static final List<String> DEFAULT_SKIP_URL = new ArrayList<>();

    static {
        DEFAULT_SKIP_URL.add("/doc.html");
        DEFAULT_SKIP_URL.add("/v2/api-docs");
        DEFAULT_SKIP_URL.add("/v3/api-docs/**");
        DEFAULT_SKIP_URL.add("/favicon.ico");
        DEFAULT_SKIP_URL.add("/error");
        DEFAULT_SKIP_URL.add("/static/**");
        DEFAULT_SKIP_URL.add("/webjars*");
        DEFAULT_SKIP_URL.add("/webjars*");
        DEFAULT_SKIP_URL.add("/webjars/**");
        DEFAULT_SKIP_URL.add("/swagger-resources/**");
    }

    @Resource
    private SsoProperties ssoProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor()).addPathPatterns("/**")
                .excludePathPatterns(DEFAULT_SKIP_URL)
                .excludePathPatterns(ssoProperties.getSkipUrl());
    }

}
