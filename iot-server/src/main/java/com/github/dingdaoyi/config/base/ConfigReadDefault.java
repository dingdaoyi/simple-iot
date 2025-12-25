package com.github.dingdaoyi.config.base;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.dromara.sms4j.provider.config.BaseConfig;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author dingyunwei
 */
@Component
@Slf4j
public class ConfigReadDefault {
    @Resource
    ReadConfig config;

    @EventListener
    public void init(ContextRefreshedEvent event){
        BaseConfig baseConfig = config.getSupplierConfig("1");
        if (baseConfig != null) {
            // 创建SmsBlend 短信实例
            SmsFactory.createSmsBlend(config, "1");
        } else {
            log.error("未找到默认短信配置");
        }

    }
}
