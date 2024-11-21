package com.github.dingdaoyi.config.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dreamlu.mica.core.spring.SpringContextUtil;
import net.dreamlu.mica.core.utils.JsonUtil;
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
    public SpringContextUtil springContextUtil(ApplicationContext applicationContext) {
        SpringContextUtil contextUtil = new SpringContextUtil();
        contextUtil.setApplicationContext(applicationContext);
        return contextUtil;
    }

    /**
     * 设置忽略空字段
     * @param objectMapper
     */
    @Autowired
    public void configJsonUtils(ObjectMapper objectMapper) {
        ObjectMapper instance = JsonUtil.getInstance();
        instance.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
}
