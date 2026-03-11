package com.github.dingdaoyi.rule.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 设备指令输出节点配置
 * @author dingyunwei
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "设备指令输出节点配置")
public class OutputCommandConfig extends NodeConfig {

    @Schema(description = "目标模式: self(当前设备) | other(其他设备)")
    private String targetMode = "self";

    @Schema(description = "目标设备ID (targetMode为other时必填)")
    private Integer targetDeviceId;

    @Schema(description = "指令类型: property(属性设置) | service(服务调用)")
    private String commandType = "service";

    @Schema(description = "服务/属性标识符")
    private String identifier;

    @Schema(description = "指令参数，支持模板变量")
    private Map<String, Object> params;
}
