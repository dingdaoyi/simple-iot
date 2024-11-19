package com.github.dingdaoyi.config.base;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author dingyunwei
 */
@Data
@Configuration
@ConfigurationProperties("sample.iot")
public class IotConfigProperties {
    /**
     * 开启设备密钥认证
     */
    private boolean enableDeviceSecret;
}
