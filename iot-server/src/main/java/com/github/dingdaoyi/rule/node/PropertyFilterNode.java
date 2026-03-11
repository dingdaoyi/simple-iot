package com.github.dingdaoyi.rule.node;

import com.github.dingdaoyi.entity.enu.RuleNodeType;
import com.github.dingdaoyi.proto.model.DeviceData;
import com.github.dingdaoyi.proto.model.tsl.TslProperty;
import com.github.dingdaoyi.rule.RuleContext;
import com.github.dingdaoyi.rule.RuleNodeExecutor;
import com.github.dingdaoyi.rule.config.FilterPropertyConfig;
import com.github.dingdaoyi.rule.config.NodeConfig;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * 属性条件过滤节点
 * @author dingyunwei
 */
@Component
public class PropertyFilterNode implements RuleNodeExecutor {

    @Override
    public RuleNodeType getNodeType() {
        return RuleNodeType.FILTER_PROPERTY;
    }

    @Override
    public NodeResult execute(RuleContext context, NodeConfig config) {
        FilterPropertyConfig cfg = (FilterPropertyConfig) config;

        String identifier = cfg.getIdentifier();
        String operator = cfg.getOperator();
        Object thresholdValue = cfg.getValue();

        if (identifier == null || operator == null || thresholdValue == null) {
            return NodeResult.fail("配置参数不完整");
        }

        // 1. 获取属性值
        Optional<DeviceData> propertyOpt = context.getProperty(identifier);
        if (propertyOpt.isEmpty()) {
            return NodeResult.of(false, "属性 " + identifier + " 不存在", "属性不存在");
        }

        DeviceData property = propertyOpt.get();
        Object actualValue = property.getValue();

        // 2. 执行条件判断
        boolean result = evaluate(actualValue, operator, thresholdValue);

        // 3. 获取物模型定义用于日志
        String unit = context.getTslProperty(identifier)
            .map(TslProperty::getUnit)
            .orElse("");

        String detail = String.format("%s=%s%s %s %s = %s",
            identifier, actualValue, unit, operator, thresholdValue, result);

        return NodeResult.of(result, detail, detail);
    }

    private boolean evaluate(Object actual, String operator, Object threshold) {
        try {
            // 处理字符串比较
            if (actual instanceof String && threshold instanceof String) {
                String actualStr = (String) actual;
                String thresholdStr = (String) threshold;
                return switch (operator) {
                    case "EQ" -> actualStr.equals(thresholdStr);
                    case "NE" -> !actualStr.equals(thresholdStr);
                    case "CONTAINS" -> actualStr.contains(thresholdStr);
                    default -> false;
                };
            }

            // 处理布尔值
            if (actual instanceof Boolean && threshold instanceof Boolean) {
                boolean actualBool = (Boolean) actual;
                boolean thresholdBool = (Boolean) threshold;
                return switch (operator) {
                    case "EQ" -> actualBool == thresholdBool;
                    case "NE" -> actualBool != thresholdBool;
                    default -> false;
                };
            }

            // 处理数值比较
            BigDecimal actualNum = new BigDecimal(actual.toString());
            BigDecimal thresholdNum = new BigDecimal(threshold.toString());

            return switch (operator) {
                case "EQ" -> actualNum.compareTo(thresholdNum) == 0;
                case "NE" -> actualNum.compareTo(thresholdNum) != 0;
                case "GT" -> actualNum.compareTo(thresholdNum) > 0;
                case "GE" -> actualNum.compareTo(thresholdNum) >= 0;
                case "LT" -> actualNum.compareTo(thresholdNum) < 0;
                case "LE" -> actualNum.compareTo(thresholdNum) <= 0;
                default -> false;
            };
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
