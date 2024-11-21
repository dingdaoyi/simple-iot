package com.github.dingdaoyi.driver.mqtt;


import com.github.dingdaoyi.driver.mqtt.model.MqttTopic;
import com.github.dingdaoyi.iot.IoTDataProcessor;
import com.github.dingdaoyi.model.DTO.DeviceDTO;
import com.github.dingdaoyi.proto.model.DeviceRequest;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.iot.mqtt.codec.MqttPublishMessage;
import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.core.server.event.IMqttMessageListener;
import net.dreamlu.iot.mqtt.spring.server.MqttServerTemplate;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Lazy;
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
public class MqttDriver implements IMqttMessageListener {

    @Resource
    private IoTDataProcessor dataProcessor;

    @Resource
    @Lazy
    private MqttServerTemplate mqttServerTemplate;

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

    private @NotNull DeviceRequest getDeviceRequest(MqttPublishMessage message, MqttTopic mqttTopic, DeviceDTO device) {
        DeviceRequest deviceRequest = new DeviceRequest();
        deviceRequest.setDeviceId(device.getId());
        deviceRequest.setProtoKey(device.getProtoKey());
        MqttDeviceConnection connection = new MqttDeviceConnection(device.getDeviceKey(),
                mqttTopic.getProductKey(), mqttServerTemplate);
        deviceRequest.setConnection(connection);
        deviceRequest.setMessageType(mqttTopic.getMessageType());
        deviceRequest.setProductKey(mqttTopic.getProductKey());
        deviceRequest.setData(message.getPayload());
        return deviceRequest;
    }
}
