package com.github.dingdaoyi.driver.mqtt;

import com.github.dingdaoyi.proto.inter.DeviceConnection;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.dreamlu.iot.mqtt.spring.server.MqttServerTemplate;
import org.tio.core.ChannelContext;

import java.io.IOException;

/**
 * mqtt 连接管理器
 */
@Data
@AllArgsConstructor
public class MqttDeviceConnection implements DeviceConnection {
    private final String deviceKey;
    private final String productKey;
    private final MqttServerTemplate mqttTemplate;

    @Override
    public boolean isConnected() {
        ChannelContext context = mqttTemplate.getChannelContext(deviceKey);
        if (context != null) {
            return !context.isClosed();
        }
        return false;
    }

    @Override
    public void disconnect() {
        ChannelContext context = mqttTemplate.getChannelContext(deviceKey);
        if (context != null) {
            context.setClosed(Boolean.TRUE);
        }
    }

    @Override
    public void sendMessage(String identifier, byte[] message) throws IOException {
        ChannelContext context = mqttTemplate.getChannelContext(deviceKey);
        if (context != null) {
            mqttTemplate.publish(deviceKey, MqttTopicConstants.getTopic(MqttTopicConstants.COMMAND_TOPIC, productKey)
                    , message);
        }
        throw new IOException("mqtt通道已关闭:" + deviceKey);
    }
}
