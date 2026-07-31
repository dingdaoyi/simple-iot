package com.github.dingdaoyi.driver.mqtt;

import com.github.dingdaoyi.config.base.IotConfigProperties;
import com.github.dingdaoyi.entity.Device;
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
        log.info("authenticate client {} user {} password {}", clientId, userName, maskSecret(password));
        if (ERROR_UNIQUE_ID.equals(uniqueId)) {
            return false;
        }

        Optional<DeviceDTO> optionalDevice = deviceService.getByDeviceKey(uniqueId);
        // 自动注册
        if (optionalDevice.isEmpty() && iotConfigProperties.isAutoRegister()) {
            optionalDevice = autoRegister(uniqueId, password);
        }
        optionalDevice.ifPresent(device -> context.set("device", device));
        return optionalDevice
                .map(item -> (!iotConfigProperties.isEnableDeviceSecret())
                                          || StringUtils.equals(password, item.getDeviceSecret()))
                .orElse(false);
    }

    /** 自动注册设备，返回新创建的 DeviceDTO */
    private Optional<DeviceDTO> autoRegister(String deviceKey, String deviceSecret) {
        Integer productId = iotConfigProperties.getAutoRegisterProductId();
        if (productId == null) {
            log.warn("自动注册失败: 未配置 simple.iot.auto-register-product-id, deviceKey={}", deviceKey);
            return Optional.empty();
        }
        Device device = new Device();
        device.setDeviceKey(deviceKey);
        device.setDeviceName(deviceKey);
        device.setProductId(productId);
        device.setOnline(false);
        device.setActiveStatus(false);
        if (StringUtils.isNotBlank(deviceSecret)) {
            device.setDeviceSecret(deviceSecret);
        }
        try {
            deviceService.save(device);
            log.info("自动注册设备成功: deviceKey={}, productId={}", deviceKey, productId);
            return deviceService.getByDeviceKey(deviceKey);
        } catch (Exception e) {
            log.warn("自动注册设备失败: deviceKey={}", deviceKey, e);
            return Optional.empty();
        }
    }

    static String maskSecret(String secret) {
        if (StringUtils.isBlank(secret)) {
            return "<empty>";
        }
        if (secret.length() <= 4) {
            return "****";
        }
        return StringUtils.repeat('*', secret.length() - 4) + secret.substring(secret.length() - 4);
    }
}
