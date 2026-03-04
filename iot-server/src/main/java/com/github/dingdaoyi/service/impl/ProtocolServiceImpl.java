package com.github.dingdaoyi.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.dingdaoyi.iot.proto.ProtocolFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dingdaoyi.mapper.ProtocolMapper;
import com.github.dingdaoyi.entity.Protocol;
import com.github.dingdaoyi.service.ProtocolService;

/**
 * @author dingyunwei
 */
@Slf4j
@Service
public class ProtocolServiceImpl extends ServiceImpl<ProtocolMapper, Protocol> implements ProtocolService {

    @Override
    public Optional<Protocol> getByProtoKey(String protoKey) {
        return Optional.ofNullable(
                getOne(
                        Wrappers
                                .<Protocol>lambdaQuery()
                                .eq(Protocol::getProtoKey, protoKey)
                )
        );
    }

    @Override
    public List<Protocol> list(String name, Integer protoType, Integer status, String protoKey, String scriptLang) {
        return lambdaQuery()
                .like(StringUtils.isNotBlank(name), Protocol::getName, name)
                .eq(protoType != null, Protocol::getProtoType, protoType)
                .eq(status != null, Protocol::getStatus, status)
                .like(StringUtils.isNotBlank(protoKey), Protocol::getProtoKey, protoKey)
                .eq(StringUtils.isNotBlank(scriptLang), Protocol::getScriptLang, scriptLang)
                .orderByAsc(Protocol::getId)
                .list();
    }

    @Override
    public boolean setProtocolStatus(Integer id, boolean enable) {
        Protocol protocol = getById(id);
        if (protocol == null) {
            log.warn("协议不存在: {}", id);
            return false;
        }

        int newStatus = enable ? 1 : 2;
        boolean updated = update(Wrappers.<Protocol>lambdaUpdate()
                .eq(Protocol::getId, id)
                .set(Protocol::getStatus, newStatus));

        if (updated) {
            // 重新加载或卸载协议
            boolean reloadResult = ProtocolFactory.reloadProtocol(protocol.getProtoKey());
            log.info("协议{}状态变更: {}, 重新加载结果: {}", enable ? "启用" : "禁用", protocol.getProtoKey(), reloadResult);
        }

        return updated;
    }
}
