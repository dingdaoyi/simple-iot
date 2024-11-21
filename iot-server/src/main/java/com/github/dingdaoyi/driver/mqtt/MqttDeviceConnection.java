package com.github.dingdaoyi.driver.mqtt;

import com.github.dingdaoyi.proto.inter.DeviceConnection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.iot.mqtt.spring.server.MqttServerTemplate;
import net.dreamlu.mica.core.utils.$;
import org.tio.core.ChannelContext;

import java.io.IOException;
import java.util.Map;

/**
 * mqtt 连接管理器
 * @author dingyunwei
 */
@Data
@AllArgsConstructor
@Slf4j
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
    public void sendMessage(Map<String,Object> metadata, byte[] message) throws IOException {
        ChannelContext context = mqttTemplate.getChannelContext(deviceKey);
        String topic = metadata.get("topic").toString();
        if (context != null) {
            mqttTemplate.publish(deviceKey,topic, message);
            return;
        }
        throw new IOException("mqtt通道已关闭:" + deviceKey);
    }
}
