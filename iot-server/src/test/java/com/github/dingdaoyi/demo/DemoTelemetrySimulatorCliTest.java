package com.github.dingdaoyi.demo;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DemoTelemetrySimulatorCliTest {

    @Test
    void defaultPayloadMatchesSeededDemoIdentifiers() {
        DemoTelemetrySimulator.Payload payload = DemoTelemetrySimulator.defaultPayload();

        assertThat(payload.deviceKey()).isEqualTo(DemoTelemetryConstants.DEVICE_KEY);
        assertThat(payload.productKey()).isEqualTo(DemoTelemetryConstants.PRODUCT_KEY);
        assertThat(payload.protoKey()).isEqualTo(DemoTelemetryConstants.PROTO_KEY);
        assertThat(payload.topic()).isEqualTo("simpleiot/pro/" + DemoTelemetryConstants.PRODUCT_KEY);
        assertThat(payload.clientId()).isEqualTo("simple_" + DemoTelemetryConstants.DEVICE_KEY);
        assertThat(payload.username()).isEqualTo(DemoTelemetryConstants.DEVICE_KEY);
        assertThat(payload.password()).isEqualTo(DemoTelemetryConstants.DEVICE_SECRET);
        assertThat(payload.json()).contains("temperature").contains("humidity").contains("voltage");
    }

    @Test
    void eventPayloadUsesSeededEventIdentifierAndEventTopic() {
        DemoTelemetrySimulator.Payload payload = DemoTelemetrySimulator.payload(Map.of("event", "true"));

        assertThat(payload.topic()).isEqualTo("simpleiot/ev/" + DemoTelemetryConstants.PRODUCT_KEY);
        assertThat(payload.json())
                .contains("\"eventIdentifier\":\"" + DemoTelemetryConstants.HIGH_TEMP_EVENT_IDENTIFIER + "\"")
                .contains("\"eventType\":\"WARN\"")
                .contains("\"temperature\":72.5");
    }

    @Test
    void payloadCanBeOverriddenForManualSmokeRuns() {
        DemoTelemetrySimulator.Payload payload = DemoTelemetrySimulator.payload(Map.of(
                "deviceKey", "dev-1",
                "productKey", "prod-1",
                "protoKey", "proto-1",
                "temperature", "66.6",
                "humidity", "55",
                "voltage", "221.7",
                "mode", "manual",
                "online", "false"
        ));

        assertThat(payload.deviceKey()).isEqualTo("dev-1");
        assertThat(payload.topic()).isEqualTo("simpleiot/pro/prod-1");
        assertThat(payload.clientId()).isEqualTo("simple_dev-1");
        assertThat(payload.json()).contains("\"temperature\":66.6");
        assertThat(payload.json()).contains("\"humidity\":55");
        assertThat(payload.json()).contains("\"voltage\":221.7");
        assertThat(payload.json()).contains("\"online\":false");
        assertThat(payload.json()).contains("\"mode\":\"manual\"");
    }

    @Test
    void payloadCanUseUnprefixedTopicsForCustomDriverConfig() {
        DemoTelemetrySimulator.Payload payload = DemoTelemetrySimulator.payload(Map.of(
                "productKey", "prod-1",
                "deviceKey", "dev-1",
                "topicPrefix", "",
                "clientIdPrefix", ""
        ));

        assertThat(payload.topic()).isEqualTo("pro/prod-1");
        assertThat(payload.clientId()).isEqualTo("dev-1");
    }
}
