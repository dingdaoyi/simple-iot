package com.github.dingdaoyi.rule.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设备分组过滤节点配置
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DeviceGroupFilterConfig extends NodeConfig {
    /** 分组ID */
    private Integer groupId;
}
