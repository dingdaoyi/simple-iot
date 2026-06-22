package com.github.dingdaoyi.controller.iot;

import com.github.dingdaoyi.controller.iot.dto.ProtocolDecodeTestRequest;
import com.github.dingdaoyi.controller.iot.dto.ProtocolEncodeTestRequest;
import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.entity.Protocol;
import com.github.dingdaoyi.entity.enu.ProtoType;
import com.github.dingdaoyi.proto.model.DecodeResult;
import com.github.dingdaoyi.proto.model.EncoderResult;
import com.github.dingdaoyi.proto.model.ProtoMessageType;
import com.github.dingdaoyi.service.ProductService;
import com.github.dingdaoyi.service.ProtocolService;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProtocolControllerScriptTest {

    private final ProtocolService protocolService = mock(ProtocolService.class);
    private final ProductService productService = mock(ProductService.class);
    private final ProtocolController controller = new ProtocolController(protocolService, productService);

    @Test
    void testDecodeDelegatesDraftRequestToProtocolService() throws Exception {
        Protocol protocol = scriptProtocol();
        DecodeResult decodeResult = new DecodeResult();
        decodeResult.setMessageId(1001);
        decodeResult.setRowData("{\"temperature\":25.5}");

        when(protocolService.testDecode(protocol, "device-001", "product-a", ProtoMessageType.PROPERTY, "{\"temperature\":25.5}"))
                .thenReturn(decodeResult);

        ProtocolDecodeTestRequest request = new ProtocolDecodeTestRequest();
        request.setProtocol(protocol);
        request.setDeviceKey("device-001");
        request.setProductKey("product-a");
        request.setMessageType(ProtoMessageType.PROPERTY);
        request.setData("{\"temperature\":25.5}");

        BaseResult<?> response = controller.testDecode(request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isSameAs(decodeResult);
        verify(protocolService).testDecode(protocol, "device-001", "product-a", ProtoMessageType.PROPERTY, "{\"temperature\":25.5}");
    }

    @Test
    void testEncodeDelegatesDraftRequestToProtocolService() throws Exception {
        Protocol protocol = scriptProtocol();
        Map<String, Object> params = Map.of("power", "on");
        EncoderResult encoderResult = new EncoderResult();
        encoderResult.setMessageId(2002);
        encoderResult.setMessage("{\"power\":\"on\"}".getBytes(StandardCharsets.UTF_8));

        when(protocolService.testEncode(protocol, "device-001", "product-a", "setPower", params))
                .thenReturn(encoderResult);

        ProtocolEncodeTestRequest request = new ProtocolEncodeTestRequest();
        request.setProtocol(protocol);
        request.setDeviceKey("device-001");
        request.setProductKey("product-a");
        request.setIdentifier("setPower");
        request.setParams(params);

        BaseResult<?> response = controller.testEncode(request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isSameAs(encoderResult);
        verify(protocolService).testEncode(protocol, "device-001", "product-a", "setPower", params);
    }

    private Protocol scriptProtocol() {
        Protocol protocol = new Protocol();
        protocol.setName("JS Debug");
        protocol.setProtoKey("js-debug");
        protocol.setProtoType(ProtoType.JAVASCRIPT);
        protocol.setScriptLang("javascript");
        protocol.setScriptContent("exports.decode = function() { return { dataList: [] }; }; exports.encode = function() { return { message: '{}' }; };");
        return protocol;
    }
}
