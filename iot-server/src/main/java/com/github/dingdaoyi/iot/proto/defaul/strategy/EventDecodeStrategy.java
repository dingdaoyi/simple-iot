package com.github.dingdaoyi.iot.proto.defaul.strategy;

import cn.hutool.core.lang.TypeReference;
import com.github.dingdaoyi.iot.proto.defaul.model.MqttEventMessage;
import com.github.dingdaoyi.iot.proto.defaul.model.MqttMessage;
import com.github.dingdaoyi.proto.model.*;
import com.github.dingdaoyi.proto.model.tsl.TslEvent;
import com.github.dingdaoyi.proto.model.tsl.TslModel;
import com.github.dingdaoyi.proto.model.tsl.TslProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.dingdaoyi.proto.model.ExceptionType.TSL_MODEL_NOT_CONFIG;

/**
 * 事件消息解码策略
 * @author dingyunwei
 */
@Component
public class EventDecodeStrategy extends AbstractMessageDecodeStrategy<MqttEventMessage> {

    @Override
    public ProtoMessageType messageType() {
        return ProtoMessageType.EVENT;
    }

    @Override
    public TypeReference<MqttMessage<MqttEventMessage>> typeReference() {
        return new TypeReference<>() {};
    }

    @Override
    public DecodeResult decode(MqttMessage<MqttEventMessage> message, TslModel tslModel, String deviceKey) throws ProtocolException {
        // 查找物模型事件定义
        TslEvent event = tslModel.eventByIdentifier(message.identifier())
                .orElseThrow(() -> protocolException(deviceKey, TSL_MODEL_NOT_CONFIG, message.messageId()));

        // 获取事件数据
        Map<String, Object> bodyData = requireBodyData(
                message.getBody() != null ? message.getBody().getData() : null,
                deviceKey,
                message.messageId()
        );

        // 解析参数
        List<TslProperty> outputParams = event.getOutputParams();
        List<DeviceData> deviceDataList = parseParams(bodyData, outputParams);

        // 构建事件数据
        DeviceEventData eventData = new DeviceEventData(event.getIdentifier(), event.getEventType());
        eventData.getParams().addAll(deviceDataList);

        // 构建结果
        DecodeResult result = buildResult(message.messageId());
        result.setEventData(eventData);

        // 构建属性映射，用于判断是否需要存储
        Map<String, TslProperty> propertyMap = outputParams.stream()
                .collect(Collectors.toMap(TslProperty::getIdentifier, Function.identity()));

        // 同时添加到属性列表（如果属性标记为需要存储）
        deviceDataList.stream()
                .filter(dd -> propertyMap.get(dd.getIdentifier()) != null && propertyMap.get(dd.getIdentifier()).isProperty())
                .forEach(result.getDataList()::add);

        return result;
    }
}
