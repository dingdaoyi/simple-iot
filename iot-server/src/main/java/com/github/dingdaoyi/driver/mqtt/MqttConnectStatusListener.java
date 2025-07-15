package com.github.dingdaoyi.driver.mqtt;

import com.github.dingdaoyi.iot.DeviceChannelManager;
import com.github.dingdaoyi.iot.IotDataProcessor;
import com.github.dingdaoyi.model.DTO.DeviceDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.mica.mqtt.core.server.event.IMqttConnectStatusListener;
import org.dromara.mica.mqtt.spring.server.MqttServerTemplate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;

/**
 * @author dingyunwei
 */
@Slf4j
@Component
public class MqttConnectStatusListener implements IMqttConnectStatusListener {
    @Resource
    private DeviceChannelManager deviceChannelManager;
    @Resource
    @Lazy
    private MqttServerTemplate mqttServerTemplate;
    @Resource
    private IotDataProcessor dataProcessor;

    @Override
    public void online(ChannelContext context, String clientId, String username) {
        log.info("online: {}", clientId);
        DeviceDTO device = context.get("device");
        if (device == null) {
            return;
        }
        device.setOnline(true);
        dataProcessor.oline(clientId);
        deviceChannelManager.addConnection(clientId, new MqttDeviceConnection(clientId, device.getProductKey(), mqttServerTemplate));
    }

    @Override
    public void offline(ChannelContext context, String clientId, String username, String reason) {
        DeviceDTO device = context.get("device");
        if (device == null) {
            return;
        }
        device.setOnline(false);
        log.info("offline: {}", clientId);
        dataProcessor.offline(clientId);
        deviceChannelManager.removeConnection(clientId);

    }
}
