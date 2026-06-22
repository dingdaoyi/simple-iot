package com.github.dingdaoyi.service;

import com.github.dingdaoyi.core.exception.BusinessException;
import com.github.dingdaoyi.entity.PushConfig;
import com.github.dingdaoyi.entity.enu.PushConfigType;
import com.github.dingdaoyi.mapper.PushConfigMapper;
import com.github.dingdaoyi.service.impl.PushConfigServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 推送配置服务测试
 * @author dingyunwei
 */
@ExtendWith(MockitoExtension.class)
class PushConfigServiceTest {

    @Mock
    private PushConfigMapper pushConfigMapper;

    private PushConfigServiceImpl pushConfigService;

    @BeforeEach
    void setUp() {
        pushConfigService = new PushConfigServiceImpl();
        ReflectionTestUtils.setField(pushConfigService, "baseMapper", pushConfigMapper);
    }

    @Test
    void saveRejectsHttpConfigWithoutHttpUrl() {
        PushConfig config = baseConfig(PushConfigType.HTTP);
        config.setHttpUrl("   ");

        assertThatThrownBy(() -> pushConfigService.save(config))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("HTTP请求URL不能为空");
    }

    @Test
    void saveRejectsHttpConfigWithUnsupportedUrlScheme() {
        PushConfig config = baseConfig(PushConfigType.HTTP);
        config.setHttpUrl("ftp://example.com/callback");

        assertThatThrownBy(() -> pushConfigService.save(config))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("HTTP请求URL必须以 http:// 或 https:// 开头");
    }

    @Test
    void saveNormalizesHttpConfigAndClearsMqttFields() {
        PushConfig config = baseConfig(PushConfigType.HTTP);
        config.setHttpUrl("  https://example.com/callback  ");
        config.setHttpMethod("post");
        config.setHttpTimeout(null);
        config.setMqttBroker("tcp://localhost:1883");
        config.setMqttTopic("device/up");
        config.setMqttQos(1);
        config.setMqttOptions(List.of(new PushConfig.KeyValue()));
        when(pushConfigMapper.insert(config)).thenReturn(1);

        pushConfigService.save(config);

        ArgumentCaptor<PushConfig> captor = ArgumentCaptor.forClass(PushConfig.class);
        verify(pushConfigMapper).insert(captor.capture());
        PushConfig saved = captor.getValue();
        assertThat(saved.getHttpUrl()).isEqualTo("https://example.com/callback");
        assertThat(saved.getHttpMethod()).isEqualTo("POST");
        assertThat(saved.getHttpTimeout()).isEqualTo(5000);
        assertThat(saved.getMqttBroker()).isNull();
        assertThat(saved.getMqttTopic()).isNull();
        assertThat(saved.getMqttOptions()).isEmpty();
    }

    @Test
    void updateRejectsMqttConfigWithWildcardPublishTopic() {
        PushConfig config = baseConfig(PushConfigType.MQTT);
        config.setId(1);
        config.setMqttBroker("tcp://localhost:1883");
        config.setMqttTopic("device/+/up");

        assertThatThrownBy(() -> pushConfigService.updateById(config))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("MQTT发布Topic不能包含通配符");
    }

    @Test
    void updateNormalizesMqttConfigAndClearsHttpFields() {
        PushConfig config = baseConfig(PushConfigType.MQTT);
        config.setId(1);
        config.setMqttBroker("  tcp://localhost:1883  ");
        config.setMqttTopic("  device/up  ");
        config.setMqttUsername("  user  ");
        config.setMqttClientId("  client-1  ");
        config.setMqttQos(null);
        config.setMqttRetain(null);
        config.setMqttKeepAlive(null);
        config.setMqttCleanSession(null);
        config.setHttpUrl("https://example.com/callback");
        config.setHttpHeaders(List.of(new PushConfig.KeyValue()));
        when(pushConfigMapper.updateById(config)).thenReturn(1);

        pushConfigService.updateById(config);

        ArgumentCaptor<PushConfig> captor = ArgumentCaptor.forClass(PushConfig.class);
        verify(pushConfigMapper).updateById(captor.capture());
        PushConfig saved = captor.getValue();
        assertThat(saved.getMqttBroker()).isEqualTo("tcp://localhost:1883");
        assertThat(saved.getMqttTopic()).isEqualTo("device/up");
        assertThat(saved.getMqttUsername()).isEqualTo("user");
        assertThat(saved.getMqttClientId()).isEqualTo("client-1");
        assertThat(saved.getMqttQos()).isZero();
        assertThat(saved.getMqttRetain()).isFalse();
        assertThat(saved.getMqttKeepAlive()).isEqualTo(60);
        assertThat(saved.getMqttCleanSession()).isTrue();
        assertThat(saved.getHttpUrl()).isNull();
        assertThat(saved.getHttpHeaders()).isEmpty();
    }

    private static PushConfig baseConfig(PushConfigType type) {
        PushConfig config = new PushConfig();
        config.setName("推送配置");
        config.setType(type);
        config.setIsEnabled(true);
        return config;
    }
}
