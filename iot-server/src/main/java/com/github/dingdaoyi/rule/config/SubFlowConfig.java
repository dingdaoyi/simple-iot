package com.github.dingdaoyi.rule.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 子规则链节点配置
 * @author dingdaoyi
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "子规则链配置")
public class SubFlowConfig extends NodeConfig {

    private static final long serialVersionUID = 1L;

    @Schema(description = "目标规则链ID")
    private Integer targetRuleChainId;

    @Schema(description = "是否将子链执行轨迹合并到父链")
    private Boolean mergeTraces = true;
}
