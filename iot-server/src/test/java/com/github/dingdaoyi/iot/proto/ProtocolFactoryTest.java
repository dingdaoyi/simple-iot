package com.github.dingdaoyi.iot.proto;

import com.github.dingdaoyi.proto.inter.DeviceConnection;
import com.github.dingdaoyi.proto.inter.ProtocolDecoder;
import com.github.dingdaoyi.proto.model.DecodeResult;
import com.github.dingdaoyi.proto.model.DeviceRequest;
import com.github.dingdaoyi.proto.model.EncoderMessage;
import com.github.dingdaoyi.proto.model.EncoderResult;
import com.github.dingdaoyi.proto.model.ProtocolException;
import com.github.dingdaoyi.proto.model.tsl.TslModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ResourceLock("ProtocolFactory.DECODERS")
class ProtocolFactoryTest {

    private Map<String, ProtocolDecoder> originalDecoders;

    @BeforeEach
    void snapshotDecoders() {
        originalDecoders = new HashMap<>(ProtocolFactory.DECODERS);
    }

    @AfterEach
    void restoreDecoders() {
        ProtocolFactory.DECODERS.clear();
        ProtocolFactory.DECODERS.putAll(originalDecoders);
    }

    @Test
    void getDecoderReturnsRegisteredDecoderByProtocolKey() {
        ProtocolDecoder decoder = new StubDecoder("demo-proto");
        ProtocolFactory.DECODERS.put("demo-proto", decoder);

        assertThat(ProtocolFactory.getDecoder("demo-proto")).containsSame(decoder);
        assertThat(ProtocolFactory.getDecoder("missing-proto")).isEmpty();
    }

    @Test
    void unloadProtocolRemovesExistingDecoderAndIsIdempotent() {
        ProtocolDecoder decoder = new StubDecoder("demo-proto");
        ProtocolFactory.DECODERS.put("demo-proto", decoder);

        assertThat(ProtocolFactory.unloadProtocol("demo-proto")).isTrue();
        assertThat(ProtocolFactory.getDecoder("demo-proto")).isEmpty();
        assertThat(ProtocolFactory.unloadProtocol("demo-proto")).isTrue();
    }

    private record StubDecoder(String protocolKey) implements ProtocolDecoder {
        @Override
        public DecodeResult decode(DeviceRequest request, TslModel tslModel) {
            return new DecodeResult();
        }

        @Override
        public EncoderResult encode(EncoderMessage message, TslModel tslModel) {
            return null;
        }

        @Override
        public void responseError(DeviceConnection connection, ProtocolException e) {
        }
    }
}
