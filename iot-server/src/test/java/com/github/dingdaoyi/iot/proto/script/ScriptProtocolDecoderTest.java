package com.github.dingdaoyi.iot.proto.script;

import com.github.dingdaoyi.iot.proto.impl.ScriptProtocolInitialize.ScriptType;
import com.github.dingdaoyi.proto.model.DataTypeEnum;
import com.github.dingdaoyi.proto.model.DecodeResult;
import com.github.dingdaoyi.proto.model.DeviceRequest;
import com.github.dingdaoyi.proto.model.EncoderMessage;
import com.github.dingdaoyi.proto.model.EncoderResult;
import com.github.dingdaoyi.proto.model.ProtoMessageType;
import com.github.dingdaoyi.proto.model.ProtocolException;
import com.github.dingdaoyi.proto.model.tsl.EventTypeEnum;
import com.github.dingdaoyi.proto.model.tsl.TslModel;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScriptProtocolDecoderTest {

    @Test
    void javascriptDecodeMapsPropertyDataListWithTypedValues() throws ProtocolException {
        ScriptProtocolDecoder decoder = decoder("""
                exports.decode = function(request) {
                  const data = JSON.parse(request.data);
                  return {
                    messageId: 101,
                    rawData: request.data,
                    dataList: [
                      { identifier: 'temperature', type: 'DOUBLE', value: data.temperature },
                      { identifier: 'online', type: 'BOOL', value: data.online },
                      { identifier: 'mode', type: 'TEXT', value: data.mode },
                      { identifier: 'settings', type: 'STRUCT', value: { threshold: data.threshold } }
                    ]
                  };
                };
                """);

        DecodeResult result = decoder.decode(request(ProtoMessageType.PROPERTY,
                "{\"temperature\":26.5,\"online\":true,\"mode\":\"auto\",\"threshold\":30}"), new TslModel());

        assertThat(result.getMessageId()).isEqualTo(101);
        assertThat(result.getRowData()).contains("temperature");
        assertThat(result.getDataList()).hasSize(4);
        assertThat(result.getDataList().get(0).getIdentifier()).isEqualTo("temperature");
        assertThat(result.getDataList().get(0).getDataType()).isEqualTo(DataTypeEnum.DOUBLE);
        assertThat(result.getDataList().get(0).getValue()).isEqualTo(26.5);
        assertThat(result.getDataList().get(1).getDataType()).isEqualTo(DataTypeEnum.BOOL);
        assertThat(result.getDataList().get(1).getValue()).isEqualTo(true);
        assertThat(result.getDataList().get(3).getDataType()).isEqualTo(DataTypeEnum.STRUCT);
        assertThat(result.getDataList().get(3).getValue()).isInstanceOf(Map.class);
    }

    @Test
    void javascriptDecodeMapsEventDataAndParameters() throws ProtocolException {
        ScriptProtocolDecoder decoder = decoder("""
                exports.decode = function(request) {
                  const data = JSON.parse(request.data);
                  return {
                    messageId: 202,
                    eventData: {
                      eventIdentifier: 'overheat',
                      eventType: 'WARN',
                      params: [
                        { identifier: 'temperature', type: 'DOUBLE', value: data.temperature },
                        { identifier: 'reason', type: 'TEXT', value: data.reason }
                      ]
                    }
                  };
                };
                """);

        DecodeResult result = decoder.decode(request(ProtoMessageType.EVENT,
                "{\"temperature\":72.3,\"reason\":\"too hot\"}"), new TslModel());

        assertThat(result.getMessageId()).isEqualTo(202);
        assertThat(result.getEventData()).isNotNull();
        assertThat(result.getEventData().getIdentifier()).isEqualTo("overheat");
        assertThat(result.getEventData().getEventType()).isEqualTo(EventTypeEnum.WARN);
        assertThat(result.getEventData().getParams()).hasSize(2);
        assertThat(result.getEventData().getParams().get(0).getIdentifier()).isEqualTo("temperature");
        assertThat(result.getEventData().getParams().get(0).getDataType()).isEqualTo(DataTypeEnum.DOUBLE);
    }

    @Test
    void javascriptDecodeMapsServiceResponseData() throws ProtocolException {
        ScriptProtocolDecoder decoder = decoder("""
                exports.decode = function() {
                  return {
                    messageId: 303,
                    serviceResData: {
                      serviceIdentifier: 'setPower',
                      resultData: [
                        { identifier: 'success', type: 'BOOL', value: true },
                        { identifier: 'message', type: 'TEXT', value: 'ok' }
                      ]
                    }
                  };
                };
                """);

        DecodeResult result = decoder.decode(request(ProtoMessageType.SERVICE_RES, "{}"), new TslModel());

        assertThat(result.getMessageId()).isEqualTo(303);
        assertThat(result.getServiceResData()).isNotNull();
        assertThat(result.getServiceResData().getIdentifier()).isEqualTo("setPower");
        assertThat(result.getServiceResData().getResultData()).hasSize(2);
        assertThat(result.getServiceResData().toMap()).containsEntry("success", true).containsEntry("message", "ok");
    }

    @Test
    void javascriptEncodeMapsMessageBytesMetadataAndNeedReply() throws ProtocolException {
        ScriptProtocolDecoder decoder = decoder("""
                exports.encode = function(message) {
                  return {
                    messageId: 404,
                    message: JSON.stringify({ id: message.identifier, params: message.params, deviceKey: message.deviceKey }),
                    needReply: false,
                    metadata: {
                      topic: '/device/' + message.deviceKey + '/command',
                      qos: 1
                    }
                  };
                };
                """);
        EncoderMessage message = new EncoderMessage();
        message.setIdentifier("setPower");
        message.setDeviceKey("device-js-001");
        message.setProductKey("product-js");
        message.setParams(Map.of("power", "on"));

        EncoderResult result = decoder.encode(message, new TslModel());

        assertThat(result.getMessageId()).isEqualTo(404);
        assertThat(new String(result.getMessage(), StandardCharsets.UTF_8))
                .contains("setPower")
                .contains("device-js-001")
                .contains("power");
        assertThat(result.isNeedReply()).isFalse();
        assertThat(result.getMetadata()).containsEntry("topic", "/device/device-js-001/command")
                .containsEntry("qos", 1L);
    }

    @Test
    void javascriptDecodeThrowsReadableProtocolExceptionWhenDecodeFunctionMissing() {
        ScriptProtocolDecoder decoder = decoder("""
                exports.encode = function() {
                  return { message: 'ok' };
                };
                """);

        assertThatThrownBy(() -> decoder.decode(request(ProtoMessageType.PROPERTY, "{}"), new TslModel()))
                .isInstanceOf(ProtocolException.class)
                .hasMessageContaining("decode");
    }

    @Test
    void javascriptDecodeWrapsScriptErrorsAsProtocolException() {
        ScriptProtocolDecoder decoder = decoder("""
                exports.decode = function() {
                  throw new Error('boom from js');
                };
                """);

        assertThatThrownBy(() -> decoder.decode(request(ProtoMessageType.PROPERTY, "{}"), new TslModel()))
                .isInstanceOf(ProtocolException.class)
                .hasMessageContaining("boom from js");
    }

    private ScriptProtocolDecoder decoder(String script) {
        return new ScriptProtocolDecoder("js-demo", "JS Demo", script, ScriptType.JAVASCRIPT);
    }

    private DeviceRequest request(ProtoMessageType messageType, String payload) {
        DeviceRequest request = new DeviceRequest();
        request.setDeviceKey("device-js-001");
        request.setProductKey("product-js");
        request.setProtoKey("js-demo");
        request.setMessageType(messageType);
        request.setData(payload.getBytes(StandardCharsets.UTF_8));
        return request;
    }
}
