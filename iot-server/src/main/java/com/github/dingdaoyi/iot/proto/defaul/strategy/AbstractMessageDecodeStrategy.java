package com.github.dingdaoyi.iot.proto.defaul.strategy;

import cn.hutool.core.collection.CollectionUtil;
import com.github.dingdaoyi.proto.model.DeviceData;
import com.github.dingdaoyi.proto.model.DecodeResult;
import com.github.dingdaoyi.proto.model.ExceptionType;
import com.github.dingdaoyi.proto.model.ProtocolException;
import com.github.dingdaoyi.proto.model.tsl.TslProperty;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 消息解码策略抽象基类
 * @param <T> 消息体类型
 * @author dingyunwei
 */
public abstract class AbstractMessageDecodeStrategy<T> implements MessageDecodeStrategy<T> {

    /**
     * 构建解码结果
     */
    protected DecodeResult buildResult(Integer messageId) {
        DecodeResult result = new DecodeResult();
        result.setMessageId(messageId);
        return result;
    }

    /**
     * 获取非空消息体数据（Map类型）
     */
    protected Map<String, Object> requireBodyData(Map<String, Object> data, String deviceKey, Integer messageId) throws ProtocolException {
        return Optional.ofNullable(data)
                .filter(CollectionUtil::isNotEmpty)
                .orElseThrow(() -> protocolException(deviceKey, ExceptionType.NULL_PARAM, messageId));
    }

    /**
     * 解析多参数数据
     * @param data 原始数据
     * @param params 物模型参数定义
     * @return 设备数据列表
     */
    protected List<DeviceData> parseParams(Map<String, Object> data, List<TslProperty> params) {
        if (CollectionUtil.isEmpty(params) || CollectionUtil.isEmpty(data)) {
            return Collections.emptyList();
        }

        Map<String, TslProperty> propertyMap = params.stream()
                .collect(Collectors.toMap(TslProperty::getIdentifier, Function.identity()));

        return data.entrySet().stream()
                .map(entry -> {
                    TslProperty property = propertyMap.get(entry.getKey());
                    if (property == null) {
                        return null;
                    }
                    return new DeviceData(
                            property.getIdentifier(),
                            property.getDataType(),
                            property.parsePropertyValue(entry.getValue())
                    );
                })
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * 创建协议异常
     */
    protected ProtocolException protocolException(String deviceKey, ExceptionType type, Integer messageId) {
        return new ProtocolException(deviceKey, type, messageId);
    }
}
