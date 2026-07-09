package com.github.dingdaoyi.rule.node;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dingdaoyi.entity.PushConfig;
import com.github.dingdaoyi.entity.enu.PushConfigType;
import com.github.dingdaoyi.entity.enu.RuleNodeType;
import com.github.dingdaoyi.rule.RuleContext;
import com.github.dingdaoyi.rule.config.OutputHttpConfig;
import com.github.dingdaoyi.service.PushConfigService;
import com.github.dingdaoyi.service.push.HttpPushDeliveryService;
import com.github.dingdaoyi.service.push.PushDeliveryResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HttpOutputNodeTest {

    @Mock
    private PushConfigService pushConfigService;
    @Mock
    private HttpPushDeliveryService httpPushDeliveryService;

    // Real ObjectMapper — ponytail: no need to mock, it's stable
    private final ObjectMapper objectMapper = new ObjectMapper();

    private HttpOutputNode node;

    @BeforeEach
    void setUp() throws Exception {
        node = new HttpOutputNode();
        for (var field : HttpOutputNode.class.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getType() == PushConfigService.class) field.set(node, pushConfigService);
            if (field.getType() == HttpPushDeliveryService.class) field.set(node, httpPushDeliveryService);
            if (field.getType() == ObjectMapper.class) field.set(node, objectMapper);
        }
    }

    private RuleContext buildContext() {
        RuleContext ctx = new RuleContext();
        ctx.setDeviceKey("dev-1");
        ctx.setDeviceName("测试设备");
        ctx.setDeviceId(1);
        return ctx;
    }

    private PushConfig buildEnabledConfig() {
        PushConfig cfg = new PushConfig();
        cfg.setId(100);
        cfg.setName("测试http");
        cfg.setType(PushConfigType.HTTP);
        cfg.setIsEnabled(true);
        cfg.setHttpUrl("https://httpbin.org/post");
        cfg.setHttpMethod("POST");
        return cfg;
    }

    @Test
    void getNodeTypeReturnsOutputHttp() {
        assertThat(node.getNodeType()).isEqualTo(RuleNodeType.OUTPUT_HTTP);
    }

    @Test
    void failsWhenPushConfigIdIsNull() {
        OutputHttpConfig config = new OutputHttpConfig(); // pushConfigId is null

        var result = node.execute(buildContext(), config);

        assertThat(result.success()).isFalse();
        assertThat(result.message()).contains("推送配置ID不能为空");
        verifyNoInteractions(pushConfigService);
    }

    @Test
    void failsWhenPushConfigNotFound() {
        when(pushConfigService.getById(100)).thenReturn(null);
        OutputHttpConfig config = new OutputHttpConfig();
        config.setPushConfigId(100);

        var result = node.execute(buildContext(), config);

        assertThat(result.success()).isFalse();
        assertThat(result.message()).contains("推送配置不存在");
    }

    @Test
    void failsWhenPushConfigDisabled() {
        PushConfig disabled = buildEnabledConfig();
        disabled.setIsEnabled(false);
        when(pushConfigService.getById(100)).thenReturn(disabled);

        OutputHttpConfig config = new OutputHttpConfig();
        config.setPushConfigId(100);

        var result = node.execute(buildContext(), config);

        assertThat(result.success()).isFalse();
        assertThat(result.message()).contains("已禁用");
    }

    @Test
    void deliversHttpPushSuccessfully() {
        PushConfig cfg = buildEnabledConfig();
        when(pushConfigService.getById(100)).thenReturn(cfg);
        when(httpPushDeliveryService.deliverHttp(any(PushConfig.class), anyString(), eq("rule.output.http")))
                .thenReturn(PushDeliveryResult.success(200, "OK", "{}"));

        OutputHttpConfig config = new OutputHttpConfig();
        config.setPushConfigId(100);

        var result = node.execute(buildContext(), config);

        assertThat(result.success()).isTrue();
        assertThat(result.message()).contains("HTTP推送");
        verify(httpPushDeliveryService).deliverHttp(any(PushConfig.class), anyString(), eq("rule.output.http"));
    }

    @Test
    void returnsFailureWhenDeliveryFails() {
        PushConfig cfg = buildEnabledConfig();
        when(pushConfigService.getById(100)).thenReturn(cfg);
        when(httpPushDeliveryService.deliverHttp(any(PushConfig.class), anyString(), eq("rule.output.http")))
                .thenReturn(PushDeliveryResult.failure("连接超时"));

        OutputHttpConfig config = new OutputHttpConfig();
        config.setPushConfigId(100);

        var result = node.execute(buildContext(), config);

        assertThat(result.success()).isFalse();
        assertThat(result.message()).isEqualTo("连接超时");
    }

    @Test
    void usesCustomBodyWhenProvided() {
        PushConfig cfg = buildEnabledConfig();
        when(pushConfigService.getById(100)).thenReturn(cfg);
        when(httpPushDeliveryService.deliverHttp(any(PushConfig.class), anyString(), eq("rule.output.http")))
                .thenReturn(PushDeliveryResult.success(200, "OK", "{}"));

        OutputHttpConfig config = new OutputHttpConfig();
        config.setPushConfigId(100);
        config.setCustomBody("{\"custom\":\"data\"}");

        node.execute(buildContext(), config);

        verify(httpPushDeliveryService).deliverHttp(any(PushConfig.class), eq("{\"custom\":\"data\"}"), eq("rule.output.http"));
    }
}
