package com.github.dingdaoyi.service;

import com.github.dingdaoyi.entity.Protocol;
import com.github.dingdaoyi.entity.enu.ProtoType;
import com.github.dingdaoyi.proto.model.DecodeResult;
import com.github.dingdaoyi.proto.model.EncoderResult;
import com.github.dingdaoyi.proto.model.ProtoMessageType;
import com.github.dingdaoyi.service.impl.ProtocolServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ProtocolServiceScriptTest {

    private final ProtocolService protocolService = new ProtocolServiceImpl();

    @Test
    void testDecodeRunsJavaScriptDraftWithoutPersistingProtocol() throws Exception {
        Protocol protocol = javascriptProtocol("""
                exports.decode = function(request) {
                  const payload = JSON.parse(request.data);
                  return {
                    messageId: payload.mid,
                    rawData: request.data,
                    dataList: [
                      { identifier: 'temperature', type: 'DOUBLE', value: payload.temperature }
                    ]
                  };
                };
                """);

        DecodeResult result = protocolService.testDecode(
                protocol,
                "device-js-debug",
                "product-js-debug",
                ProtoMessageType.PROPERTY,
                "{\"mid\": 2001, \"temperature\": 23.6}"
        );

        assertThat(result.getMessageId()).isEqualTo(2001);
        assertThat(result.getRowData()).contains("temperature");
        assertThat(result.getDataList()).hasSize(1);
        assertThat(result.getDataList().getFirst().getIdentifier()).isEqualTo("temperature");
        assertThat(result.getDataList().getFirst().getValue()).isEqualTo(23.6D);
    }

    @Test
    void testEncodeRunsJavaScriptDraftWithoutPersistingProtocol() throws Exception {
        Protocol protocol = javascriptProtocol("""
                exports.encode = function(message) {
                  return {
                    messageId: 3002,
                    message: JSON.stringify({ identifier: message.identifier, params: message.params }),
                    needReply: true,
                    metadata: { topic: '/device/' + message.deviceKey + '/command' }
                  };
                };
                """);

        EncoderResult result = protocolService.testEncode(
                protocol,
                "device-js-debug",
                "product-js-debug",
                "setPower",
                Map.of("power", true)
        );

        assertThat(result.getMessageId()).isEqualTo(3002);
        assertThat(new String(result.getMessage())).contains("setPower", "power");
        assertThat(result.isNeedReply()).isTrue();
        assertThat(result.getMetadata()).containsEntry("topic", "/device/device-js-debug/command");
    }

    private Protocol javascriptProtocol(String scriptContent) {
        Protocol protocol = new Protocol();
        protocol.setName("JS 调试协议");
        protocol.setProtoKey("js-debug-draft");
        protocol.setProtoType(ProtoType.JAVASCRIPT);
        protocol.setScriptLang("javascript");
        protocol.setScriptContent(scriptContent);
        return protocol;
    }
}
