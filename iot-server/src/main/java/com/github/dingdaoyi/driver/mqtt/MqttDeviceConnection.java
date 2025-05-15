package com.github.dingdaoyi.driver.mqtt;

import com.github.dingdaoyi.proto.inter.DeviceConnection;
import com.github.dingdaoyi.proto.model.ExceptionType;
import com.github.dingdaoyi.proto.model.ProtocolException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.dromara.mica.mqtt.spring.server.MqttServerTemplate;
import org.tio.core.ChannelContext;

import java.io.IOException;
import java.util.Map;

/**
 * mqtt 连接管理器
 *
 * @author dingyunwei
 */
@Slf4j
public record MqttDeviceConnection(String deviceKey, String productKey, MqttServerTemplate mqttTemplate) implements DeviceConnection {
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
    public void sendMessage(Map<String, Object> metadata, byte[] message) throws ProtocolException {
        ChannelContext context = mqttTemplate.getChannelContext(deviceKey);
        String topic = metadata.get("topic").toString();
        if (context != null) {
            mqttTemplate.publish(deviceKey, topic, message);
            return;
        }
        throw new ProtocolException(deviceKey, ExceptionType.DEVICE_NOT_CONNECTED);
    }
}
