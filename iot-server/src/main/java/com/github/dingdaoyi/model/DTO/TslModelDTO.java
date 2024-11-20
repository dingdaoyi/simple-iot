package com.github.dingdaoyi.model.DTO;

import lombok.Data;

import java.util.List;
import java.util.Optional;

/**
 * 物模型
 * @author dingyunwei
 */
@Data
public class TslModelDTO {

    /**
     * 产品key
     */
    private String productKey;

    /**
     * 物模型事件
     */
    private List<TslEventDTO> events;

    /**
     * 物模型服务
     */
    private List<TslServiceDTO> services;

    /**
     * 物模型属性
     */
    private List<TslPropertyDTO> properties;

    public Optional<TslPropertyDTO> propertyByIdentifier(String identifier) {
        return properties.stream().filter(p -> p.getIdentifier().equals(identifier)).findFirst();
    }
    public Optional<TslEventDTO> eventByIdentifier(String identifier) {
        return events.stream().filter(p -> p.getIdentifier().equals(identifier)).findFirst();
    }
    public Optional<TslServiceDTO> serviceByIdentifier(String identifier) {
        return services.stream().filter(p -> p.getIdentifier().equals(identifier)).findFirst();
    }
}
