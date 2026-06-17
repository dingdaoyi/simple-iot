package com.github.dingdaoyi.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.github.dingdaoyi.entity.SmsConfig;
import com.github.dingdaoyi.mapper.SmsConfigMapper;
import com.github.dingdaoyi.service.impl.SmsConfigServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * 短信配置服务测试
 * @author dingyunwei
 */
@ExtendWith(MockitoExtension.class)
class SmsConfigServiceTest {

    @Mock
    private SmsConfigMapper smsConfigMapper;

    @Mock
    private SmsTemplateService smsTemplateService;

    private SmsConfigServiceImpl smsConfigService;

    @BeforeEach
    void setUp() {
        smsConfigService = new SmsConfigServiceImpl();
        ReflectionTestUtils.setField(smsConfigService, "baseMapper", smsConfigMapper);
        ReflectionTestUtils.setField(smsConfigService, "smsTemplateService", smsTemplateService);
    }

    @Test
    void getDefaultConfigReturnsEnabledDefaultConfig() {
        SmsConfig expected = new SmsConfig();
        expected.setId(1);
        expected.setIsDefault(true);
        expected.setStatus(1);
        when(smsConfigMapper.selectOne(any(Wrapper.class), eq(true))).thenReturn(expected);

        Optional<SmsConfig> config = smsConfigService.getDefaultConfig();

        assertThat(config).contains(expected);
    }

    @Test
    void sendSmsReturnsFalseWhenDefaultConfigMissing() {
        when(smsConfigMapper.selectOne(any(Wrapper.class), eq(true))).thenReturn(null);

        Boolean result = smsConfigService.sendSms("13800138000", "测试短信内容");

        assertThat(result).isFalse();
    }
}
