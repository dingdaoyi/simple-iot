package com.github.dingdaoyi.driver.mqtt.model;

import com.github.dingdaoyi.driver.mqtt.MqttTopicConstants;
import com.github.dingdaoyi.proto.model.ProtoMessageType;
import lombok.Data;

import java.util.Optional;
import java.util.regex.Matcher;

@Data
public class MqttTopic {

    /**
     * 产品key
     */
    private String productKey;

    /**
     * 上报数据类型
     */
    private ProtoMessageType messageType;

    public static Optional<MqttTopic> parse(String topic) {
        final Matcher matcher = MqttTopicConstants.TOPIC_PARSE_PATTERN
                .matcher(topic);
        if (!matcher.find()) {
            return Optional.empty();
        }
        MqttTopic mqttTopic = new MqttTopic();
        mqttTopic.setProductKey(matcher.group(2));
        mqttTopic.setMessageType(ProtoMessageType.fromCode(matcher.group(1)).orElse(null));
        return Optional.of(mqttTopic);
    }

}
