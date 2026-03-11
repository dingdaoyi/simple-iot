package com.github.dingdaoyi.entity.enu;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 规则节点类型 - 基于物模型
 * @author dingyunwei
 */
@Getter
public enum RuleNodeType {

    // ==================== 输入节点 ====================
    INPUT_PROPERTY("属性上报", NodeCategory.INPUT, "设备上报属性数据"),
    INPUT_EVENT("事件上报", NodeCategory.INPUT, "设备上报事件"),
    INPUT_ONLINE("设备上下线", NodeCategory.INPUT, "设备上下线"),

    // ==================== 过滤节点 ====================
    FILTER_PROPERTY("属性条件", NodeCategory.FILTER, "基于物模型属性过滤"),
    FILTER_EVENT_TYPE("事件类型", NodeCategory.FILTER, "按事件类型过滤"),
    FILTER_SCRIPT("脚本过滤", NodeCategory.FILTER, "自定义脚本过滤"),

    // ==================== 告警节点 ====================
    ALARM_CREATE("创建告警", NodeCategory.ALARM, "创建告警记录"),
    ALARM_CLEAR("清除告警", NodeCategory.ALARM, "清除已有告警"),

    // ==================== 输出节点 ====================
    OUTPUT_MESSAGE("消息推送", NodeCategory.OUTPUT, "复用现有消息通知"),
    OUTPUT_HTTP("HTTP推送", NodeCategory.OUTPUT, "引用推送配置，推送到外部API"),
    OUTPUT_MQTT("MQTT推送", NodeCategory.OUTPUT, "引用推送配置，转发到其他Topic"),
    OUTPUT_COMMAND("设备指令", NodeCategory.OUTPUT, "下发RPC指令");

    @EnumValue
    @JsonValue
    private final String value;
    private final String name;
    private final NodeCategory category;
    private final String description;

    RuleNodeType(String name, NodeCategory category, String description) {
        this.value = this.name();
        this.name = name;
        this.category = category;
        this.description = description;
    }

    /**
     * 节点分类
     */
    @Getter
    public enum NodeCategory {
        INPUT("输入", "#10b981"),
        FILTER("过滤", "#3b82f6"),
        ALARM("告警", "#ef4444"),
        OUTPUT("输出", "#8b5cf6");

        private final String name;
        private final String color;

        NodeCategory(String name, String color) {
            this.name = name;
            this.color = color;
        }
    }
}
