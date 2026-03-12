package com.github.dingdaoyi.config.base;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author dingyunwei
 */
@Configuration
public class MyBatisPlusConfig {

    @Resource
    private ObjectMapper objectMapper;

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.POSTGRE_SQL));
        return interceptor;
    }

    /**
     * 配置 MyBatis-Plus JacksonTypeHandler 使用 Spring 管理的 ObjectMapper
     * 支持 LocalDateTime 等 Java 8 时间类型序列化
     */
    @PostConstruct
    public void initJacksonTypeHandler() {
        JacksonTypeHandler.setObjectMapper(objectMapper);
    }
}