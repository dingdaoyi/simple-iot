package com.github.dingdaoyi.driver;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author dingyunwei
 */
@Configuration
@ConfigurationProperties(prefix = "sample.iot.driver")
@Data
public class DriverConfig {
    /**
     * mqtt配置
     */
    private MqttConfigProperties mqtt;

    @Data
    public static class MqttConfigProperties {
        /**
         * 主题前缀
         */
        private String topicPrefix;

        /**
         * mqtt客户端前缀
         */
        private String mqttClientIdPrefix;
    }
}
