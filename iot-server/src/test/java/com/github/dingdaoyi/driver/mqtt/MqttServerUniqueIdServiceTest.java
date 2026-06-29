package com.github.dingdaoyi.driver.mqtt;

import com.github.dingdaoyi.driver.DriverConfig;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MqttServerUniqueIdServiceTest {

    @Test
    void extractsDeviceKeyWhenConfiguredPrefixMatchesClientId() {
        MqttServerUniqueIdService service = new MqttServerUniqueIdService();
        service.initMqttClientIdPrefix(driverConfig("simple_"));

        assertThat(service.getUniqueId(null, "simple_demo-sensor-001", "ignored-user", "secret"))
                .isEqualTo("demo-sensor-001");
    }

    @Test
    void fallsBackToUsernameWhenPrefixDoesNotMatch() {
        MqttServerUniqueIdService service = new MqttServerUniqueIdService();
        service.initMqttClientIdPrefix(driverConfig("simple_"));

        assertThat(service.getUniqueId(null, "demo-sensor-001", "demo-sensor-001", "secret"))
                .isEqualTo("demo-sensor-001");
    }

    @Test
    void rejectsWhenClientIdAndUsernameCannotIdentifyDevice() {
        MqttServerUniqueIdService service = new MqttServerUniqueIdService();
        service.initMqttClientIdPrefix(driverConfig("simple_"));

        assertThat(service.getUniqueId(null, "unknown-client", "", "secret"))
                .isEqualTo(MqttServerUniqueIdService.ERROR_UNIQUE_ID);
    }

    private static DriverConfig driverConfig(String prefix) {
        DriverConfig driverConfig = new DriverConfig();
        DriverConfig.MqttConfigProperties mqtt = new DriverConfig.MqttConfigProperties();
        mqtt.setMqttClientIdPrefix(prefix);
        driverConfig.setMqtt(mqtt);
        return driverConfig;
    }
}
