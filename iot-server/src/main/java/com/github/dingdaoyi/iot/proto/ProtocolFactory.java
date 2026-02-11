package com.github.dingdaoyi.iot.proto;

import cn.hutool.extra.spring.SpringUtil;
import com.github.dingdaoyi.entity.Protocol;
import com.github.dingdaoyi.entity.enu.ProtoType;
import com.github.dingdaoyi.proto.inter.ProtocolDecoder;
import com.github.dingdaoyi.service.ProtocolService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 协议工厂
 * 负责管理和初始化所有协议解析器
 *
 * @author dingyunwei
 */
@Slf4j
@Component
public class ProtocolFactory implements ApplicationListener<ApplicationReadyEvent> {

    /**
     * 协议解析器缓存
     * key: protocolKey
     * value: ProtocolDecoder 实例
     */
    public static final Map<String, ProtocolDecoder> DECODERS = new HashMap<>();

    @Resource
    private ProtocolService protocolService;

    /**
     * 获取协议解析器
     *
     * @param protoKey 协议key
     * @return 协议解析器
     */
    public static Optional<ProtocolDecoder> getDecoder(String protoKey) {
        return Optional.ofNullable(DECODERS.get(protoKey));
    }

    /**
     * 重新加载指定协议
     *
     * @param protoKey 协议key
     * @return 是否成功
     */
    public static boolean reloadProtocol(String protoKey) {
        ProtocolService protocolService = SpringUtil.getBean(ProtocolService.class);
        Protocol protocol = protocolService.lambdaQuery()
                .eq(Protocol::getProtoKey, protoKey)
                .one();

        if (protocol == null) {
            log.warn("协议不存在: {}", protoKey);
            return false;
        }

        // 如果协议是禁用状态，则卸载
        if (!protocol.isEnabled()) {
            return unloadProtocol(protoKey);
        }

        try {
            ProtocolInitialize initializer = SpringUtil.getBean(protocol.getProtoType().getInitClass());
            Map<String, ProtocolDecoder> newDecoders = initializer.initProtocol(List.of(protocol));

            if (newDecoders.containsKey(protoKey)) {
                DECODERS.put(protoKey, newDecoders.get(protoKey));
                log.info("协议重新加载成功: {}", protoKey);
                return true;
            }

            log.warn("协议重新加载失败，未返回解析器: {}", protoKey);
            return false;
        } catch (Exception e) {
            log.error("协议重新加载异常: {}", protoKey, e);
            return false;
        }
    }

    /**
     * 卸载指定协议
     *
     * @param protoKey 协议key
     * @return 是否成功
     */
    public static boolean unloadProtocol(String protoKey) {
        ProtocolDecoder removed = DECODERS.remove(protoKey);
        if (removed != null) {
            log.info("协议已卸载: {}", protoKey);
            return true;
        }
        log.warn("协议未加载，无需卸载: {}", protoKey);
        return true;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        initProtocols();
    }

    /**
     * 初始化所有协议
     */
    private void initProtocols() {
        log.info("开始初始化协议...");

        List<Protocol> protocolList = protocolService.lambdaQuery()
                .eq(Protocol::getStatus, 1)
                .list();
        if (protocolList == null || protocolList.isEmpty()) {
            log.warn("没有配置任何启用的协议");
            return;
        }

        // 按协议类型分组
        Map<ProtoType, List<Protocol>> protoMap = protocolList.stream()
                .collect(Collectors.groupingBy(Protocol::getProtoType));

        int successCount = 0;
        int failCount = 0;

        // 遍历每种协议类型，使用对应的初始化器
        for (Map.Entry<ProtoType, List<Protocol>> entry : protoMap.entrySet()) {
            ProtoType protoType = entry.getKey();
            List<Protocol> protocols = entry.getValue();

            try {
                ProtocolInitialize initializer = SpringUtil.getBean(protoType.getInitClass());
                Map<String, ProtocolDecoder> decoders = initializer.initProtocol(protocols);

                if (decoders != null && !decoders.isEmpty()) {
                    DECODERS.putAll(decoders);
                    successCount += decoders.size();
                    log.info("{} 协议初始化成功 {}/{}", protoType, decoders.size(), protocols.size());
                } else {
                    failCount += protocols.size();
                    log.warn("{} 协议初始化失败，未返回任何解析器", protoType);
                }
            } catch (Exception e) {
                failCount += protocols.size();
                log.error("{} 协议初始化异常", protoType, e);
            }
        }

        log.info("协议初始化完成，成功: {}，失败: {}，总计: {}", successCount, failCount, protocolList.size());
    }
}