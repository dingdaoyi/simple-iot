package com.github.dingdaoyi.config.base;

import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author dingyunwei
 */
@Configuration
public class MicaConfig {
    @Bean
    public SpringUtil springContextUtil(ApplicationContext applicationContext) {
        SpringUtil contextUtil = new SpringUtil();
        contextUtil.setApplicationContext(applicationContext);
        return contextUtil;
    }

    /**
     * 设置忽略空字段
     * @param objectMapper
     */
    @Autowired
    public void configJsonUtils(ObjectMapper objectMapper) {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
}
