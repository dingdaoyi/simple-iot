package com.github.dingdaoyi.iot.proto.defaul;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.github.dingdaoyi.driver.mqtt.MqttTopicConstants;
import com.github.dingdaoyi.iot.proto.defaul.model.*;
import com.github.dingdaoyi.proto.inter.DeviceConnection;
import com.github.dingdaoyi.proto.model.*;
import com.github.dingdaoyi.proto.model.tsl.TslEvent;
import com.github.dingdaoyi.proto.model.tsl.TslModel;
import com.github.dingdaoyi.proto.model.tsl.TslProperty;
import com.github.dingdaoyi.proto.inter.ProtocolDecoder;
import com.github.dingdaoyi.proto.model.tsl.TslService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tio.utils.json.JsonUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

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
    public DecodeResult decode(DeviceRequest request, TslModel tslModel) throws ProtocolException {
        if (log.isDebugEnabled()) {
            log.debug("收到解析数据:{}", new String(request.getData(), StandardCharsets.UTF_8));
        }
        byte[] data = request.getData();
        DecodeResult decodeResult = new DecodeResult();
        Integer messageId = -1;
        try {
            switch (request.getMessageType()) {
                case PROPERTY -> {
                    MqttMessage<MqttPopMessage> mqttMessage = JSONUtil.toBean(new String(data,StandardCharsets.UTF_8), new TypeReference<>() {
                    },true);
                    if (mqttMessage != null) {
                        messageId = mqttMessage.getHeader().getMsgId();
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
                        decodeResult.getDataList()
                                .add(new DeviceData(mqttMessage.identifier(), dataType, tslPropertyDTO.parsePropertyValue(popMessage.getValue())));
                    }
                }
                case EVENT -> {
                    MqttMessage<MqttEventMessage> mqttMessage = JSONUtil.toBean(new String(data,StandardCharsets.UTF_8), new TypeReference<>() {
                    },true);
                    if (mqttMessage != null) {
                        messageId = mqttMessage.getHeader().getMsgId();
                        Optional<TslEvent> tslEventOpt = tslModel.eventByIdentifier(mqttMessage.identifier());
                        if (tslEventOpt.isEmpty()) {
                            throw new ProtocolException(request.getDeviceKey(), ExceptionType.TSL_MODEL_NOT_CONFIG, mqttMessage.messageId());
                        }
                        TslEvent tslEvent = tslEventOpt.get();
                        List<TslProperty> outputParams = tslEvent.getOutputParams();
                        DeviceEventData eventData = new DeviceEventData(tslEvent.getIdentifier(), tslEvent.getEventType());
                        if (CollectionUtil.isNotEmpty(outputParams)) {
                            MqttEventMessage message = mqttMessage.getBody();
                            if (message == null || CollectionUtil.isEmpty(message.getData())) {
                                throw new ProtocolException(request.getDeviceKey(), ExceptionType.NULL_PARAM, mqttMessage.messageId());
                            }
                            Map<String, TslProperty> propertiesMap = outputParams.stream().collect(Collectors.toMap(TslProperty::getIdentifier, v -> v));
                            for (Map.Entry<String, Object> entry : message.getData().entrySet()) {
                                TslProperty tslProperty = propertiesMap.get(entry.getKey());
                                if (tslProperty == null) {
                                    throw new ProtocolException(request.getDeviceKey(), ExceptionType.NULL_CONFIGURED_PARAMETER, mqttMessage.messageId());
                                }
                                decodeResult.setEventData(eventData);
                                DeviceData deviceData = new DeviceData(tslProperty.getIdentifier(), tslProperty.getDataType(), tslProperty.parsePropertyValue(entry.getValue()));
                                eventData.getParams().add(deviceData);
                                if (tslProperty.isProperty()) {
                                    decodeResult.getDataList().add(deviceData);
                                }
                            }
                        }
                    }
                }
                case SERVICE_RES -> {
                    MqttMessage<MqttServiceResMessage> mqttMessage = JSONUtil.toBean(new String(data,StandardCharsets.UTF_8), new TypeReference<>() {
                    },true);
                    if (mqttMessage != null) {
                        messageId = mqttMessage.getHeader().getMsgId();
                        Optional<TslService> tslServiceOpt = tslModel.serviceByIdentifier(mqttMessage.identifier());
                        if (tslServiceOpt.isEmpty()) {
                            throw new ProtocolException(request.getDeviceKey(), ExceptionType.TSL_MODEL_NOT_CONFIG, mqttMessage.messageId());
                        }
                        TslService tslService = tslServiceOpt.get();
                        List<TslProperty> outputParams = tslService.getOutputParams();
                        DeviceServiceResData serviceResData = new DeviceServiceResData(tslService.getIdentifier());
                        if (CollectionUtil.isNotEmpty(outputParams)) {
                            MqttServiceResMessage message = mqttMessage.getBody();
                            if (message == null || CollectionUtil.isEmpty(message.getData())) {
                                throw new ProtocolException(request.getDeviceKey(), ExceptionType.NULL_PARAM, mqttMessage.messageId());
                            }
                            Map<String, TslProperty> propertiesMap = outputParams.stream().collect(Collectors.toMap(TslProperty::getIdentifier, v -> v));
                            for (Map.Entry<String, Object> entry : message.getData().entrySet()) {
                                TslProperty tslProperty = propertiesMap.get(entry.getKey());
                                if (tslProperty == null) {
                                    throw new ProtocolException(request.getDeviceKey(), ExceptionType.NULL_CONFIGURED_PARAMETER, mqttMessage.messageId());
                                }
                                DeviceData deviceData = new DeviceData(tslProperty.getIdentifier(), tslProperty.getDataType(), tslProperty.parsePropertyValue(entry.getValue()));
                                serviceResData.getResultData().add(deviceData);
                                if (tslProperty.isProperty()) {
                                    decodeResult.getDataList().add(deviceData);
                                }
                                decodeResult.setServiceResData(serviceResData);
                            }
                        }
                    }
                }
            }
            decodeResult.setMessageId(messageId);
            decodeResult.setRowData(new String(data, StandardCharsets.UTF_8));
        } catch (IllegalArgumentException e) {
            throw new ProtocolException(request.getDeviceKey(), ExceptionType.INVALID_PARAM, messageId);
        }
        return decodeResult;
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
