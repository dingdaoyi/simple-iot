package com.github.dingdaoyi.model.vo;

import com.github.dingdaoyi.rule.RuleContext;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 规则链调试结果。
 *
 * @author dingyunwei
 */
@Data
@Schema(description = "规则链调试结果")
public class RuleChainDebugResultVo {

    @Schema(description = "规则链ID，草稿调试时可能为空")
    private Integer ruleChainId;

    @Schema(description = "规则链名称")
    private String ruleChainName;

    @Schema(description = "是否整体执行成功，任一节点失败则为 false")
    private boolean success;

    @Schema(description = "总耗时，毫秒")
    private long durationMs;

    @Schema(description = "执行节点数量")
    private int executedNodeCount;

    @Schema(description = "执行轨迹")
    private List<RuleContext.ExecutionTrace> traces;
}
