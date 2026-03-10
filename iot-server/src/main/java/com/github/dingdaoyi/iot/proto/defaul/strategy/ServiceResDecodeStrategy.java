package com.github.dingdaoyi.iot.proto.defaul.strategy;

import cn.hutool.core.lang.TypeReference;
import com.github.dingdaoyi.iot.proto.defaul.model.MqttMessage;
import com.github.dingdaoyi.iot.proto.defaul.model.MqttServiceResMessage;
import com.github.dingdaoyi.proto.model.*;
import com.github.dingdaoyi.proto.model.tsl.TslModel;
import com.github.dingdaoyi.proto.model.tsl.TslProperty;
import com.github.dingdaoyi.proto.model.tsl.TslService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.dingdaoyi.proto.model.ExceptionType.TSL_MODEL_NOT_CONFIG;

/**
 * 服务响应消息解码策略
 * @author dingyunwei
 */
@Component
public class ServiceResDecodeStrategy extends AbstractMessageDecodeStrategy<MqttServiceResMessage> {

    @Override
    public ProtoMessageType messageType() {
        return ProtoMessageType.SERVICE_RES;
    }

    @Override
    public TypeReference<MqttMessage<MqttServiceResMessage>> typeReference() {
        return new TypeReference<>() {};
    }

    @Override
    public DecodeResult decode(MqttMessage<MqttServiceResMessage> message, TslModel tslModel, String deviceKey) throws ProtocolException {
        // 查找物模型服务定义
        TslService service = tslModel.serviceByIdentifier(message.identifier())
                .orElseThrow(() -> protocolException(deviceKey, TSL_MODEL_NOT_CONFIG, message.messageId()));

        // 获取服务响应数据
        Map<String, Object> bodyData = requireBodyData(
                message.getBody() != null ? message.getBody().getData() : null,
                deviceKey,
                message.messageId()
        );

        // 解析参数
        List<TslProperty> outputParams = service.getOutputParams();
        List<DeviceData> deviceDataList = parseParams(bodyData, outputParams);

        // 构建服务响应数据
        DeviceServiceResData serviceResData = new DeviceServiceResData(service.getIdentifier());
        serviceResData.getResultData().addAll(deviceDataList);

        // 构建结果
        DecodeResult result = buildResult(message.messageId());
        result.setServiceResData(serviceResData);

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
