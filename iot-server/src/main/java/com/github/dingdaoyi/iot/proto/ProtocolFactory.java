package com.github.dingdaoyi.iot.proto;

import com.github.dingdaoyi.entity.Protocol;
import com.github.dingdaoyi.entity.enu.ProtoType;
import com.github.dingdaoyi.iot.proto.defaul.DefaultProtocolDecoder;
import com.github.dingdaoyi.proto.inter.ProtocolDecoder;
import com.github.dingdaoyi.service.ProtocolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author dingyunwei
 */
@Slf4j
@Component
public class ProtocolFactory implements ApplicationListener<ApplicationReadyEvent> {

    public static final Map<String, ProtocolDecoder> DECODERS = new HashMap<>();

    private static ProtocolService protocolService;

    private static ApplicationContext applicationContext;


    /**
     * 获取协议
     *
     * @param protoKey
     * @return
     */
    public static Optional<ProtocolDecoder> getDecoder(String protoKey) {
        return Optional.ofNullable(DECODERS.get(protoKey));
    }


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        applicationContext = event.getApplicationContext();
        DefaultProtocolDecoder defaultProtocolDecoder = applicationContext.getBean(DefaultProtocolDecoder.class);
        DECODERS.put(defaultProtocolDecoder.protocolKey(), defaultProtocolDecoder);
        protocolService= applicationContext.getBean(ProtocolService.class);
        init();
    }

    private static void init() {
        List<Protocol> protocolList = protocolService.list();
        Map<ProtoType, List<Protocol>> protoMap = protocolList.stream()
                .collect(Collectors.groupingBy(Protocol::getProtoType));
        for (Map.Entry<ProtoType, List<Protocol>> entry : protoMap.entrySet()) {
            ProtoType protoType = entry.getKey();
            DECODERS.putAll(applicationContext.getBean(protoType.getInitClass())
                    .initProtocol(entry.getValue()));
        }
        log.info("协议初始化完成...");
    }
}
