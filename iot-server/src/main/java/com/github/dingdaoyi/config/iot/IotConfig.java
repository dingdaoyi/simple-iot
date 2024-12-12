package com.github.dingdaoyi.config.iot;

import com.github.dingdaoyi.config.base.IotConfigProperties;
import com.github.dingdaoyi.iot.DeviceChannelManager;
import com.github.dingdaoyi.iot.impl.PartitionedDeviceChannelManager;
import com.github.dingdaoyi.iot.impl.SingleDeviceChannelManager;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author dingyunwei
 */
@Configuration
public class IotConfig {

    @Resource
    private IotConfigProperties iotConfigProperties;

    @Bean(destroyMethod = "close")
    DeviceChannelManager deviceChannelManager() {
        if (iotConfigProperties.getDeviceChannelType() == IotConfigProperties.DeviceChannelType.SINGLE) {
            return new SingleDeviceChannelManager();
        }
        return new PartitionedDeviceChannelManager();
    }
}
