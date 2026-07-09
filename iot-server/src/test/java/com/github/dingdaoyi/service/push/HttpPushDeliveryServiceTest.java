package com.github.dingdaoyi.service.push;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dingdaoyi.entity.PushConfig;
import com.github.dingdaoyi.entity.enu.PushConfigType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class HttpPushDeliveryServiceTest {

    @Test
    void deliverHttpAddsStandardHmacWebhookHeaders() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        HttpPushDeliveryService service = service(restTemplate, true);
        PushConfig config = httpConfig("https://example.com/webhook");
        config.setHttpSignEnabled(true);
        config.setHttpSignSecret("demo-secret");

        String body = "{\"temperature\":36.5}";
        String expectedSignature = new PushWebhookSigner().sign("demo-secret", "1710000000000", body);
        server.expect(requestTo("https://example.com/webhook"))
            .andExpect(method(HttpMethod.POST))
            .andExpect(header("X-Simple-IoT-Timestamp", "1710000000000"))
            .andExpect(header("X-Simple-IoT-Signature", expectedSignature))
            .andExpect(header("X-Simple-IoT-Signature-Method", "HMAC-SHA256"))
            .andExpect(header("X-Simple-IoT-Event", "telemetry.property"))
            .andExpect(content().json(body))
            .andRespond(withSuccess("{\"ok\":true}", MediaType.APPLICATION_JSON));

        PushDeliveryResult result = service.deliverHttp(config, body, "telemetry.property");

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(200);
        assertThat(result.getMessage()).contains("HTTP推送成功");
        server.verify();
    }

    @Test
    void deliverHttpBlocksPrivateNetworkEndpointWhenGuardIsDisabled() {
        HttpPushDeliveryService service = service(new RestTemplate(), false);
        PushConfig config = httpConfig("http://127.0.0.1:8080/webhook");

        PushDeliveryResult result = service.deliverHttp(config, "{}", "telemetry.property");

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).contains("不允许推送到内网或本机地址");
    }

    @Test
    void deliverHttpAllowsPrivateNetworkEndpointWhenGuardIsEnabledForDemoOrTests() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        HttpPushDeliveryService service = service(restTemplate, true);
        PushConfig config = httpConfig("http://127.0.0.1:8080/webhook");

        server.expect(requestTo("http://127.0.0.1:8080/webhook"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("ok", MediaType.TEXT_PLAIN));

        PushDeliveryResult result = service.deliverHttp(config, Map.of("ok", true), "push.test");

        assertThat(result.isSuccess()).isTrue();
        server.verify();
    }

    private HttpPushDeliveryService service(RestTemplate restTemplate, boolean allowPrivateEndpoints) {
        return new HttpPushDeliveryService(
            restTemplate,
            new ObjectMapper(),
            new PushWebhookSigner(),
            new PushEndpointGuard(allowPrivateEndpoints),
            null, // ponytail: no mapper in unit tests, saveLog will silently skip
            Clock.fixed(Instant.ofEpochMilli(1710000000000L), ZoneOffset.UTC)
        );
    }

    private PushConfig httpConfig(String url) {
        PushConfig config = new PushConfig();
        config.setId(1);
        config.setName("Webhook");
        config.setType(PushConfigType.HTTP);
        config.setIsEnabled(true);
        config.setHttpUrl(url);
        config.setHttpMethod("POST");
        config.setHttpTimeout(5000);
        return config;
    }
}
