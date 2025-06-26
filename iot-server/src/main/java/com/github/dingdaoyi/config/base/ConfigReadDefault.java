package com.github.dingdaoyi.config.base;

import jakarta.annotation.Resource;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author dingyunwei
 */
@Component
public class ConfigReadDefault {
    @Resource
    ReadConfig config;

    @EventListener
    public void init(ContextRefreshedEvent event){
        // 创建SmsBlend 短信实例
        SmsFactory.createSmsBlend(config,"1");
    }
}
