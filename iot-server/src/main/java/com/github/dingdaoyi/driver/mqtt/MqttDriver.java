package com.github.dingdaoyi.driver.mqtt;


import com.github.dingdaoyi.driver.mqtt.model.MqttTopic;
import com.github.dingdaoyi.iot.DeviceChannelManager;
import com.github.dingdaoyi.iot.IotDataProcessor;
import com.github.dingdaoyi.model.DTO.DeviceDTO;
import com.github.dingdaoyi.proto.model.DeviceRequest;
import com.github.dingdaoyi.core.driver.DeviceTransport;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.mica.mqtt.codec.MqttQoS;
import org.dromara.mica.mqtt.codec.message.MqttPublishMessage;
import org.dromara.mica.mqtt.core.server.event.IMqttMessageListener;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;

import java.util.Optional;

/**
 * mqtt驱动
 *
 * @author dingyunwei
 */
@Component
@Slf4j
public class MqttDriver implements IMqttMessageListener, DeviceTransport {

    @Resource
    private IotDataProcessor dataProcessor;

    @Resource
    private DeviceChannelManager deviceChannelManager;

    private volatile boolean running = false;

    @Override
    public void start() {
        running = true;
        // 启动MQTT服务端/客户端，订阅主题等
    }

    @Override
    public void stop() {
        running = false;
        // 断开MQTT连接等
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public String getType() {
        return "MQTT";
    }

    @Override
    public String getName() {
        return "MQTT标准驱动";
    }

    @Override
    public void onMessage(ChannelContext context, String clientId, String topic, MqttQoS qoS, MqttPublishMessage message) {
        log.info("topic:{},clientId:{},message:{}", topic, clientId, message);
        DeviceDTO device = context.get("device");
        if (device == null) {
            //TODO, 后续做自动注册,需要放开
            log.error("设备不存在,账号未校验通过");
            return;
        }
        Optional<MqttTopic> optionalMqttTopic = MqttTopic.parse(topic);
        if (optionalMqttTopic.isEmpty()) {
            log.error("上传数据的topic错误,无法进行后续解析:{}|{}", clientId, topic);
            return;
        }
        DeviceRequest deviceRequest = getDeviceRequest(message, optionalMqttTopic.get(), device);
        dataProcessor.messageUp(deviceRequest);
    }

    private DeviceRequest getDeviceRequest(MqttPublishMessage message, MqttTopic mqttTopic, DeviceDTO device) {
        DeviceRequest deviceRequest = new DeviceRequest();
        deviceRequest.setProtoKey(device.getProtoKey());
        deviceRequest.setDeviceKey(device.getDeviceKey());
        deviceRequest.setConnection(deviceChannelManager.getConnection(device.getDeviceKey()));
        deviceRequest.setMessageType(mqttTopic.getMessageType());
        deviceRequest.setProductKey(mqttTopic.getProductKey());
        deviceRequest.setData(message.getPayload());
        return deviceRequest;
    }
}
