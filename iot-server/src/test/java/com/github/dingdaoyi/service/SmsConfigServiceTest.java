package com.github.dingdaoyi.service;

import com.github.dingdaoyi.entity.SmsConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.annotation.Resource;
import java.util.Optional;

/**
 * 短信配置服务测试
 * @author dingyunwei
 */
@SpringBootTest
public class SmsConfigServiceTest {
    
    @Resource
    private SmsConfigService smsConfigService;
    
    @Test
    public void testGetDefaultConfig() {
        Optional<SmsConfig> config = smsConfigService.getDefaultConfig();
        System.out.println("默认配置: " + config.orElse(null));
    }
    
    @Test
    public void testSendSms() {
        Boolean result = smsConfigService.sendSms("13800138000", "测试短信内容");
        System.out.println("发送结果: " + result);
    }
}