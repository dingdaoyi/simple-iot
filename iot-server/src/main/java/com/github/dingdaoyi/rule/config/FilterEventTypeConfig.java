package com.github.dingdaoyi.rule.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 事件类型过滤节点配置
 * @author dingyunwei
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "事件类型过滤节点配置")
public class FilterEventTypeConfig extends NodeConfig {

    @Schema(description = "事件标识符")
    private String identifier;

    @Schema(description = "事件参数标识符")
    private String paramIdentifier;

    @Schema(description = "操作符: GT, GE, EQ, LT, LE, NE")
    private String operator;

    @Schema(description = "阈值")
    private String value;
}
