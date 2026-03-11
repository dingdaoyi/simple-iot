package com.github.dingdaoyi.rule.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * HTTP推送输出节点配置
 * @author dingyunwei
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "HTTP推送输出节点配置")
public class OutputHttpConfig extends NodeConfig {

    @Schema(description = "推送配置ID")
    private Integer pushConfigId;

    @Schema(description = "自定义请求体模板，支持变量 ${deviceName} 等")
    private String customBody;
}
