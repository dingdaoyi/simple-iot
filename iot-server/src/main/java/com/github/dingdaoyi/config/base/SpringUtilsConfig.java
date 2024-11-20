package com.github.dingdaoyi.config.base;

import net.dreamlu.mica.core.spring.SpringContextUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author dingyunwei
 */
@Configuration
public class SpringUtilsConfig {
    @Bean
    public SpringContextUtil springContextUtil(ApplicationContext applicationContext) {
        SpringContextUtil contextUtil = new SpringContextUtil();
        contextUtil.setApplicationContext(applicationContext);
        return contextUtil;
    }
}
