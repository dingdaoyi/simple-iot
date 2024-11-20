package com.github.dingdaoyi.model.DTO;

import lombok.Data;

import java.util.List;

/**
 * 物模型
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
}
