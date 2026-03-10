package com.github.dingdaoyi.iot.proto.defaul;

import cn.hutool.json.JSONUtil;
import com.github.dingdaoyi.driver.mqtt.MqttTopicConstants;
import com.github.dingdaoyi.iot.proto.defaul.model.*;
import com.github.dingdaoyi.iot.proto.defaul.strategy.MessageDecodeStrategy;
import com.github.dingdaoyi.proto.inter.DeviceConnection;
import com.github.dingdaoyi.proto.model.*;
import com.github.dingdaoyi.proto.model.tsl.TslModel;
import com.github.dingdaoyi.proto.inter.ProtocolDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tio.utils.json.JsonUtil;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 默认协议解码器
 * 使用策略模式处理不同类型的消息
 * @author dingyunwei
 */
@Slf4j
@Service
public class DefaultProtocolDecoder implements ProtocolDecoder {

    private final Map<ProtoMessageType, MessageDecodeStrategy<?>> strategyMap;

    public DefaultProtocolDecoder(List<MessageDecodeStrategy<?>> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(MessageDecodeStrategy::messageType, Function.identity()));
    }

    @Override
    public String protocolKey() {
        return "system-default";
    }

    @Override
    public DecodeResult decode(DeviceRequest request, TslModel tslModel) throws ProtocolException {
        String jsonData = new String(request.getData(), StandardCharsets.UTF_8);
        log.debug("收到设备[{}]解析数据:{}", request.getDeviceKey(), jsonData);

        MessageDecodeStrategy<?> strategy = strategyMap.get(request.getMessageType());
        if (strategy == null) {
            throw new ProtocolException(request.getDeviceKey(), ExceptionType.INVALID_PARAM, -1);
        }

        try {
            DecodeResult result = doDecode(strategy, jsonData, tslModel, request.getDeviceKey());
            result.setRowData(jsonData);
            return result;
        } catch (IllegalArgumentException e) {
            throw new ProtocolException(request.getDeviceKey(), ExceptionType.INVALID_PARAM, -1);
        }
    }

    /**
     * 执行解码
     */
    private <T> DecodeResult doDecode(MessageDecodeStrategy<T> strategy, String json,
                                       TslModel tslModel, String deviceKey) throws ProtocolException {
        MqttMessage<T> message = JSONUtil.toBean(json, strategy.typeReference(), true);
        return strategy.decode(message, tslModel, deviceKey);
    }

    @Override
    public EncoderResult encode(EncoderMessage message, TslModel tslModel) throws ProtocolException {
        MqttMessage<MqttServiceMessage> mqttMessage = new MqttMessage<>();
        MqttHeader header = new MqttHeader(message.getIdentifier());
        mqttMessage.setHeader(header);
        mqttMessage.setBody(new MqttServiceMessage(message.getParams()));
        EncoderResult encoderResult = new EncoderResult();
        encoderResult.setMessageId(header.getMsgId());
        encoderResult.setMessage(JsonUtil.toJsonBytes(mqttMessage));
        HashMap<String, Object> meta = new HashMap<>();
        meta.put("topic", MqttTopicConstants.getTopic(MqttTopicConstants.COMMAND_TOPIC, message.getProductKey()));
        encoderResult.setMetadata(meta);
        return encoderResult;
    }

    @Override
    public void responseError(DeviceConnection connection, ProtocolException e) {
        try {
            HashMap<String, Object> params = new HashMap<>();
            params.put("topic", MqttTopicConstants.ERROR_RESPONSE_TOPIC);
            MqttMessage<MqttErrors> message = new MqttMessage<>();
            MqttHeader header = new MqttHeader();
            header.setMsgId(e.getMessageId());
            message.setHeader(header);
            message.setBody(new MqttErrors(e));
            connection.sendMessage(params, JsonUtil.toJsonBytes(message));
        } catch (ProtocolException ex) {
            log.warn("消息发送异常:{}", ex.getMessage());
        }
    }
}
