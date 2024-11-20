package com.github.dingdaoyi.proto.impl;

import com.github.dingdaoyi.entity.Protocol;
import com.github.dingdaoyi.proto.ProtocolInitialize;
import com.github.dingdaoyi.proto.inter.ProtocolDecoder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.core.spring.SpringContextUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dingyunwei
 */
@Service
@Slf4j
public class SystemProtocolInitialize implements ProtocolInitialize {

    @Override
    public Map<String, ProtocolDecoder> initProtocol(List<Protocol> protocols) {
        Map<String, ProtocolDecoder> result = new HashMap<>();
        for (Protocol protocol : protocols) {
            Class<? extends ProtocolDecoder> clazz;
            try {
                clazz = (Class<? extends ProtocolDecoder>) Class.forName(protocol.getHandlerClass());
            } catch (ClassNotFoundException e) {
                log.error("加载系统协议失败,协议类不存在:{}", protocol.getHandlerClass());
                continue;
            }
            result.put(protocol.getProtoKey(), SpringContextUtil.getBean(clazz));
        }

        return result;
    }
}
