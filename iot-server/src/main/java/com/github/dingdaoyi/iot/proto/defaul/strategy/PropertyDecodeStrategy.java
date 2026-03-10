package com.github.dingdaoyi.iot.proto.defaul.strategy;

import cn.hutool.core.lang.TypeReference;
import com.github.dingdaoyi.iot.proto.defaul.model.MqttMessage;
import com.github.dingdaoyi.iot.proto.defaul.model.MqttPopMessage;
import com.github.dingdaoyi.proto.model.DeviceData;
import com.github.dingdaoyi.proto.model.DecodeResult;
import com.github.dingdaoyi.proto.model.ProtoMessageType;
import com.github.dingdaoyi.proto.model.ProtocolException;
import com.github.dingdaoyi.proto.model.tsl.TslModel;
import com.github.dingdaoyi.proto.model.tsl.TslProperty;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.github.dingdaoyi.proto.model.ExceptionType.NULL_PARAM;
import static com.github.dingdaoyi.proto.model.ExceptionType.TSL_MODEL_NOT_CONFIG;

/**
 * 属性消息解码策略
 * @author dingyunwei
 */
@Component
public class PropertyDecodeStrategy extends AbstractMessageDecodeStrategy<MqttPopMessage> {

    @Override
    public ProtoMessageType messageType() {
        return ProtoMessageType.PROPERTY;
    }

    @Override
    public TypeReference<MqttMessage<MqttPopMessage>> typeReference() {
        return new TypeReference<>() {};
    }

    @Override
    public DecodeResult decode(MqttMessage<MqttPopMessage> message, TslModel tslModel, String deviceKey) throws ProtocolException {
        // 查找物模型属性定义
        TslProperty property = tslModel.propertyByIdentifier(message.identifier())
                .orElseThrow(() -> protocolException(deviceKey, TSL_MODEL_NOT_CONFIG, message.messageId()));

        // 获取属性值
        Object value = Optional.ofNullable(message.getBody())
                .map(MqttPopMessage::getValue)
                .orElseThrow(() -> protocolException(deviceKey, NULL_PARAM, message.messageId()));

        // 构建结果
        DecodeResult result = buildResult(message.messageId());
        result.getDataList().add(new DeviceData(
                message.identifier(),
                property.getDataType(),
                property.parsePropertyValue(value)
        ));
        return result;
    }
}
