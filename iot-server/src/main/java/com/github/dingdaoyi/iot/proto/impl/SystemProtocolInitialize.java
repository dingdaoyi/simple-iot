package com.github.dingdaoyi.iot.proto.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.github.dingdaoyi.entity.Protocol;
import com.github.dingdaoyi.iot.proto.ProtocolInitialize;
import com.github.dingdaoyi.proto.inter.ProtocolDecoder;
import lombok.extern.slf4j.Slf4j;
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
                clazz =(Class<? extends ProtocolDecoder>) Thread.currentThread().getContextClassLoader()
                        .loadClass(protocol.getHandlerClass());
            } catch (ClassNotFoundException e) {
                log.error("加载系统协议失败,协议类不存在:{}", protocol.getHandlerClass());
                continue;
            }
            result.put(protocol.getProtoKey(), SpringUtil.getBean(clazz));
        }

        return result;
    }
}
