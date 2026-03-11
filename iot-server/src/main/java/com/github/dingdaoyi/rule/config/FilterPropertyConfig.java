package com.github.dingdaoyi.rule.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 属性条件过滤节点配置
 * @author dingyunwei
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "属性条件过滤节点配置")
public class FilterPropertyConfig extends NodeConfig {

    @Schema(description = "属性标识符")
    private String identifier;

    @Schema(description = "比较操作符: EQ, NE, GT, GE, LT, LE, CONTAINS")
    private String operator;

    @Schema(description = "比较值")
    private Object value;
}
