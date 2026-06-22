package com.github.dingdaoyi.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 规则链草稿校验结果。
 *
 * @author dingyunwei
 */
@Data
@Schema(description = "规则链草稿校验结果")
public class RuleChainValidationResultVo {

    @Schema(description = "是否校验通过")
    private boolean valid = true;

    @Schema(description = "错误列表，存在错误时不建议保存/运行")
    private List<Issue> errors = new ArrayList<>();

    @Schema(description = "告警列表，可保存但需要用户确认")
    private List<Issue> warnings = new ArrayList<>();

    @Schema(description = "从输入节点可达的节点ID列表")
    private List<String> reachableNodeIds = new ArrayList<>();

    public void addError(String code, String nodeId, String connectionId, String message) {
        errors.add(new Issue(code, nodeId, connectionId, message));
        valid = false;
    }

    public void addWarning(String code, String nodeId, String connectionId, String message) {
        warnings.add(new Issue(code, nodeId, connectionId, message));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "规则链校验问题")
    public static class Issue {
        @Schema(description = "问题编码")
        private String code;

        @Schema(description = "关联节点ID")
        private String nodeId;

        @Schema(description = "关联连接ID")
        private String connectionId;

        @Schema(description = "问题描述")
        private String message;
    }
}
