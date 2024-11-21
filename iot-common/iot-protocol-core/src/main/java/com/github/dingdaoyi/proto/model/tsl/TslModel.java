package com.github.dingdaoyi.proto.model.tsl;

import lombok.Data;

import java.util.List;
import java.util.Optional;

/**
 * 物模型
 * @author dingyunwei
 */
@Data
public class TslModel {

    /**
     * 产品key
     */
    private String productKey;

    /**
     * 物模型事件
     */
    private List<TslEvent> events;

    /**
     * 物模型服务
     */
    private List<TslService> services;

    /**
     * 物模型属性
     */
    private List<TslProperty> properties;

    public Optional<TslProperty> propertyByIdentifier(String identifier) {
        return properties.stream().filter(p -> p.getIdentifier().equals(identifier)).findFirst();
    }
    public Optional<TslEvent> eventByIdentifier(String identifier) {
        return events.stream().filter(p -> p.getIdentifier().equals(identifier)).findFirst();
    }
    public Optional<TslService> serviceByIdentifier(String identifier) {
        return services.stream().filter(p -> p.getIdentifier().equals(identifier)).findFirst();
    }
}
