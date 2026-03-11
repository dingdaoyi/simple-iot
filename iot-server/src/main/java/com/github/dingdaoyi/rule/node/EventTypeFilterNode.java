package com.github.dingdaoyi.rule.node;

import com.github.dingdaoyi.entity.enu.RuleNodeType;
import com.github.dingdaoyi.proto.model.DeviceData;
import com.github.dingdaoyi.proto.model.DeviceEventData;
import com.github.dingdaoyi.rule.RuleContext;
import com.github.dingdaoyi.rule.RuleNodeExecutor;
import com.github.dingdaoyi.rule.config.FilterEventTypeConfig;
import com.github.dingdaoyi.rule.config.NodeConfig;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * 事件类型过滤节点
 * 按事件标识符和参数条件过滤
 * @author dingyunwei
 */
@Component
public class EventTypeFilterNode implements RuleNodeExecutor {

    @Override
    public RuleNodeType getNodeType() {
        return RuleNodeType.FILTER_EVENT_TYPE;
    }

    @Override
    public NodeResult execute(RuleContext context, NodeConfig config) {
        // 获取事件数据
        DeviceEventData eventData = context.getEventData().orElse(null);
        if (eventData == null) {
            return NodeResult.of(false, "没有事件数据", null);
        }

        FilterEventTypeConfig cfg = (FilterEventTypeConfig) config;

        String identifier = cfg.getIdentifier();
        if (identifier == null || identifier.isEmpty()) {
            return NodeResult.of(true, "未配置事件过滤，放行", null);
        }

        // 检查事件标识符是否匹配
        if (!identifier.equals(eventData.getIdentifier())) {
            return NodeResult.of(false, "事件不匹配: " + eventData.getIdentifier(), null);
        }

        // 如果配置了参数过滤
        String paramIdentifier = cfg.getParamIdentifier();
        if (paramIdentifier != null && !paramIdentifier.isEmpty()) {
            List<DeviceData> params = eventData.getParams();
            if (params == null || params.isEmpty()) {
                return NodeResult.of(false, "事件参数为空", null);
            }

            // 查找指定参数
            DeviceData paramData = params.stream()
                .filter(p -> paramIdentifier.equals(p.getIdentifier()))
                .findFirst()
                .orElse(null);

            if (paramData == null) {
                return NodeResult.of(false, "事件参数不存在: " + paramIdentifier, null);
            }

            Object paramValue = paramData.getValue();
            String operator = cfg.getOperator();
            String thresholdStr = cfg.getValue();

            if (operator != null && thresholdStr != null) {
                boolean paramMatch = compareValue(paramValue, operator, thresholdStr);
                if (!paramMatch) {
                    return NodeResult.of(false, "参数条件不满足: " + paramIdentifier + " " + operator + " " + thresholdStr, null);
                }
            }
        }

        return NodeResult.of(true, "事件匹配: " + identifier, null);
    }

    /**
     * 比较参数值
     */
    private boolean compareValue(Object actualValue, String operator, String thresholdStr) {
        if (actualValue == null || thresholdStr == null) {
            return false;
        }

        try {
            BigDecimal actual = new BigDecimal(String.valueOf(actualValue));
            BigDecimal threshold = new BigDecimal(thresholdStr);

            return switch (operator) {
                case "GT" -> actual.compareTo(threshold) > 0;
                case "GE" -> actual.compareTo(threshold) >= 0;
                case "EQ" -> actual.compareTo(threshold) == 0;
                case "LT" -> actual.compareTo(threshold) < 0;
                case "LE" -> actual.compareTo(threshold) <= 0;
                case "NE" -> actual.compareTo(threshold) != 0;
                default -> false;
            };
        } catch (NumberFormatException e) {
            // 非数值比较，使用字符串比较
            String actualStr = String.valueOf(actualValue);
            return switch (operator) {
                case "EQ" -> actualStr.equals(thresholdStr);
                case "NE" -> !actualStr.equals(thresholdStr);
                default -> false;
            };
        }
    }
}
