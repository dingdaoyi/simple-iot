package com.github.dingdaoyi.iot.proto.defaul;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.dingdaoyi.driver.mqtt.MqttTopicConstants;
import com.github.dingdaoyi.iot.proto.defaul.model.MqttErrors;
import com.github.dingdaoyi.iot.proto.defaul.model.MqttHeader;
import com.github.dingdaoyi.iot.proto.defaul.model.MqttMessage;
import com.github.dingdaoyi.proto.inter.DeviceConnection;
import com.github.dingdaoyi.proto.model.*;
import com.github.dingdaoyi.iot.proto.defaul.model.MqttPopMessage;
import com.github.dingdaoyi.proto.model.tsl.TslModel;
import com.github.dingdaoyi.proto.model.tsl.TslProperty;
import com.github.dingdaoyi.proto.inter.ProtocolDecoder;
import com.github.dingdaoyi.service.DeviceService;
import com.github.dingdaoyi.service.TslModelService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.core.utils.JsonUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Optional;

/**
 * @author dingyunwei
 */
@Service
@Slf4j
public class DefaultProtocolDecoder implements ProtocolDecoder {

    @Override
    public String protocolKey() {
        return "system-default";
    }

    @Override
    public DecodeResult decode(DeviceRequest request,TslModel tslModel) throws ProtocolException {
        if (log.isDebugEnabled()) {
            log.debug("收到解析数据:{}", new String(request.getData(), StandardCharsets.UTF_8));
        }
        byte[] data = request.getData();
        DecodeResult decodeResult = new DecodeResult();
        switch (request.getMessageType()) {
            case PROPERTY -> {
                MqttMessage<MqttPopMessage> mqttMessage = JsonUtil.readValue(data, new TypeReference<>() {
                });
                if (mqttMessage != null) {
                    Optional<TslProperty> propertyDTO = tslModel.propertyByIdentifier(mqttMessage.identifier());
                    if (propertyDTO.isEmpty()) {
                        throw new ProtocolException(request.getDeviceKey(), ExceptionType.TSL_MODEL_NOT_CONFIG, mqttMessage.messageId());
                    }
                    TslProperty tslPropertyDTO = propertyDTO.get();
                    DataTypeEnum dataType = tslPropertyDTO.getDataType();
                    MqttPopMessage popMessage = mqttMessage.getBody();
                    if (popMessage == null || popMessage.getValue() == null) {
                        throw new ProtocolException(request.getDeviceKey(), ExceptionType.NULL_PARAM, mqttMessage.messageId());
                    }
                    try {
                        decodeResult.getDataList()
                                .add(new DeviceData(mqttMessage.identifier(), dataType, dataType.parse(popMessage.getValue())));
                    } catch (IllegalArgumentException e) {
                        throw new ProtocolException(request.getDeviceKey(), ExceptionType.INVALID_PARAM, mqttMessage.messageId());
                    }
                }
            }
            case EVENT -> {

            }
            case SERVICE_RES -> {

            }
        }
        decodeResult.setRowData(new String(data, StandardCharsets.UTF_8));
        return decodeResult;
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
            connection.sendMessage(params, JsonUtil.toJsonAsBytes(message));
        } catch (IOException ex) {
            log.warn("消息发送异常:{}", ex.getMessage());
        }
    }
}
