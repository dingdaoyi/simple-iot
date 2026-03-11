package com.github.dingdaoyi.rule.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 创建告警节点配置
 * @author dingyunwei
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "创建告警节点配置")
public class AlarmCreateConfig extends NodeConfig {

    @Schema(description = "告警类型标识")
    private String alarmType;

    @Schema(description = "告警名称，支持模板变量 ${deviceName}")
    private String alarmName;

    @Schema(description = "严重程度: WARNING, MINOR, MAJOR, CRITICAL")
    private String severity = "WARNING";

    @Schema(description = "告警消息，支持模板变量")
    private String message;
}
