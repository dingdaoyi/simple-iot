package com.github.dingdaoyi.driver.mqtt;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MqttServerAuthHandlerTest {

    @Test
    void maskSecretKeepsOnlyLastFourCharacters() {
        assertThat(MqttServerAuthHandler.maskSecret("device-secret-1234"))
                .isEqualTo("**************1234");
    }

    @Test
    void maskSecretHidesShortSecretsCompletely() {
        assertThat(MqttServerAuthHandler.maskSecret("1234")).isEqualTo("****");
        assertThat(MqttServerAuthHandler.maskSecret("123")).isEqualTo("****");
    }

    @Test
    void maskSecretHandlesBlankValues() {
        assertThat(MqttServerAuthHandler.maskSecret(null)).isEqualTo("<empty>");
        assertThat(MqttServerAuthHandler.maskSecret("")).isEqualTo("<empty>");
        assertThat(MqttServerAuthHandler.maskSecret("   ")).isEqualTo("<empty>");
    }
}
