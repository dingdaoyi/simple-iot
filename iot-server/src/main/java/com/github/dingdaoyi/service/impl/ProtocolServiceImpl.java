package com.github.dingdaoyi.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.dingdaoyi.iot.proto.ProtocolFactory;
import com.github.dingdaoyi.iot.proto.impl.ScriptProtocolInitialize.ScriptType;
import com.github.dingdaoyi.iot.proto.script.ScriptProtocolDecoder;
import com.github.dingdaoyi.proto.model.DecodeResult;
import com.github.dingdaoyi.proto.model.DeviceRequest;
import com.github.dingdaoyi.proto.model.EncoderMessage;
import com.github.dingdaoyi.proto.model.EncoderResult;
import com.github.dingdaoyi.proto.model.ExceptionType;
import com.github.dingdaoyi.proto.model.ProtoMessageType;
import com.github.dingdaoyi.proto.model.ProtocolException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
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

    @Override
    public DecodeResult testDecode(Protocol protocol, String deviceKey, String productKey,
                                   ProtoMessageType messageType, String data) throws ProtocolException {
        ScriptProtocolDecoder decoder = createDraftDecoder(protocol, deviceKey);

        DeviceRequest request = new DeviceRequest();
        request.setDeviceKey(StringUtils.defaultIfBlank(deviceKey, "debug-device"));
        request.setProductKey(productKey);
        request.setProtoKey(protocol.getProtoKey());
        request.setMessageType(messageType == null ? ProtoMessageType.PROPERTY : messageType);
        request.setData(StringUtils.defaultString(data).getBytes(StandardCharsets.UTF_8));

        return decoder.decode(request, null);
    }

    @Override
    public EncoderResult testEncode(Protocol protocol, String deviceKey, String productKey,
                                    String identifier, Map<String, Object> params) throws ProtocolException {
        ScriptProtocolDecoder decoder = createDraftDecoder(protocol, deviceKey);

        EncoderMessage message = new EncoderMessage();
        message.setDeviceKey(StringUtils.defaultIfBlank(deviceKey, "debug-device"));
        message.setProductKey(productKey);
        message.setIdentifier(identifier);
        message.setParams(params == null ? Map.of() : params);

        return decoder.encode(message, null);
    }

    private ScriptProtocolDecoder createDraftDecoder(Protocol protocol, String deviceKey) throws ProtocolException {
        if (protocol == null) {
            throw new ProtocolException(deviceKey, ExceptionType.INVALID_PARAM, -1, "协议草稿不能为空");
        }
        if (!protocol.isScriptType()) {
            throw new ProtocolException(deviceKey, ExceptionType.INVALID_PARAM, -1, "仅支持脚本协议调试");
        }
        if (!protocol.hasScriptContent()) {
            throw new ProtocolException(deviceKey, ExceptionType.INVALID_PARAM, -1, "脚本内容不能为空");
        }

        return new ScriptProtocolDecoder(
                StringUtils.defaultIfBlank(protocol.getProtoKey(), "debug-script"),
                StringUtils.defaultIfBlank(protocol.getName(), "脚本调试协议"),
                protocol.getScriptContent(),
                parseScriptType(protocol.getScriptLang())
        );
    }

    private ScriptType parseScriptType(String scriptLang) {
        if (StringUtils.isBlank(scriptLang)) {
            return ScriptType.JAVASCRIPT;
        }
        return switch (scriptLang.toLowerCase()) {
            case "js", "javascript" -> ScriptType.JAVASCRIPT;
            case "py", "python" -> ScriptType.PYTHON;
            case "groovy" -> ScriptType.GROOVY;
            case "lua" -> ScriptType.LUA;
            default -> ScriptType.JAVASCRIPT;
        };
    }
}
