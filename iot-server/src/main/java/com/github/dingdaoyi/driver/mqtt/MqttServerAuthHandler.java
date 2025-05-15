package com.github.dingdaoyi.driver.mqtt;

import com.github.dingdaoyi.config.base.IotConfigProperties;
import com.github.dingdaoyi.model.DTO.DeviceDTO;
import com.github.dingdaoyi.service.DeviceService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.mica.mqtt.core.server.auth.IMqttServerAuthHandler;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;

import java.util.Optional;

import static com.github.dingdaoyi.driver.mqtt.MqttServerUniqueIdService.ERROR_UNIQUE_ID;

/**
 * @author dingyunwei
 */
@Slf4j
@Component
public class MqttServerAuthHandler implements IMqttServerAuthHandler {

    @Resource
    private DeviceService deviceService;

    @Resource
    private IotConfigProperties iotConfigProperties;

    @Override
    public boolean authenticate(ChannelContext context, String uniqueId, String clientId, String userName, String password) {
        log.info("authenticate client {} user {} password {}", clientId, userName, password);
        if (ERROR_UNIQUE_ID.equals(uniqueId)) {
            return false;
        }

        Optional<DeviceDTO> optionalDevice = deviceService.getByDeviceKey(uniqueId);
        optionalDevice.ifPresent(device -> context.set("device", device));
        return optionalDevice
                .map(item -> (!iotConfigProperties.isEnableDeviceSecret())
                                          || StringUtils.equals(password, item.getDeviceSecret()))
                .orElse(false);
    }
}
