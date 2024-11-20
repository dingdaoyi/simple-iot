package com.github.dingdaoyi.iot.proto.impl;

import com.github.dingdaoyi.entity.Protocol;
import com.github.dingdaoyi.iot.proto.ProtocolInitialize;
import com.github.dingdaoyi.proto.inter.ProtocolDecoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ScriptProtocolInitialize  implements ProtocolInitialize {
    @Override
    public Map<String, ProtocolDecoder> initProtocol(List<Protocol> protocols) {
        return Map.of();
    }
}
