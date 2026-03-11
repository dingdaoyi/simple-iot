package com.github.dingdaoyi.rule.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设备上下线输入节点配置
 * @author dingyunwei
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "设备上下线输入节点配置")
public class InputOnlineConfig extends NodeConfig {

    @Schema(description = "监听的上下线状态: online-上线, offline-下线, all-全部")
    private String onlineStatus = "all";
}
