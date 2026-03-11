package com.github.dingdaoyi.rule;

import com.github.dingdaoyi.entity.enu.RuleNodeType;
import com.github.dingdaoyi.rule.config.NodeConfig;

import java.util.Map;

/**
 * 规则节点执行器接口
 * @author dingyunwei
 */
public interface RuleNodeExecutor {

    /**
     * 获取节点类型
     */
    RuleNodeType getNodeType();

    /**
     * 执行节点
     * @param context 执行上下文
     * @param config 节点配置
     * @return 执行结果
     */
    NodeResult execute(RuleContext context, NodeConfig config);

    /**
     * 节点执行结果
     */
    record NodeResult(
        boolean success,
        String connectionType, // Success, Failure, True, False
        String message,
        Map<String, Object> data
    ) {
        public static NodeResult ok() {
            return new NodeResult(true, "Success", null, null);
        }

        public static NodeResult ok(Map<String, Object> data) {
            return new NodeResult(true, "Success", null, data);
        }

        public static NodeResult ok(String connectionType) {
            return new NodeResult(true, connectionType, null, null);
        }

        /**
         * 成功结果（带消息）
         */
        public static NodeResult success(String message) {
            return new NodeResult(true, "Success", message, null);
        }

        /**
         * 成功结果（带消息和数据）
         */
        public static NodeResult success(String message, Map<String, Object> data) {
            return new NodeResult(true, "Success", message, data);
        }

        public static NodeResult fail(String message) {
            return new NodeResult(false, "Failure", message, null);
        }

        public static NodeResult of(boolean value) {
            return new NodeResult(true, value ? "True" : "False", null, null);
        }

        public static NodeResult of(boolean value, String trueDetail, String falseDetail) {
            return new NodeResult(true, value ? "True" : "False", value ? trueDetail : falseDetail, null);
        }
    }
}
