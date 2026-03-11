package com.github.dingdaoyi.rule.config;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 节点配置基类
 * 使用 Jackson 多态反序列化，根据 nodeType 字段自动识别具体类型
 * @author dingyunwei
 */
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "nodeType")
@JsonSubTypes({
    // 输入节点
    @JsonSubTypes.Type(value = InputPropertyConfig.class, name = "INPUT_PROPERTY"),
    @JsonSubTypes.Type(value = InputEventConfig.class, name = "INPUT_EVENT"),
    @JsonSubTypes.Type(value = InputOnlineConfig.class, name = "INPUT_ONLINE"),
    // 过滤节点
    @JsonSubTypes.Type(value = FilterPropertyConfig.class, name = "FILTER_PROPERTY"),
    @JsonSubTypes.Type(value = FilterEventTypeConfig.class, name = "FILTER_EVENT_TYPE"),
    @JsonSubTypes.Type(value = FilterScriptConfig.class, name = "FILTER_SCRIPT"),
    // 告警节点
    @JsonSubTypes.Type(value = AlarmCreateConfig.class, name = "ALARM_CREATE"),
    @JsonSubTypes.Type(value = AlarmClearConfig.class, name = "ALARM_CLEAR"),
    // 输出节点
    @JsonSubTypes.Type(value = OutputMessageConfig.class, name = "OUTPUT_MESSAGE"),
    @JsonSubTypes.Type(value = OutputHttpConfig.class, name = "OUTPUT_HTTP"),
    @JsonSubTypes.Type(value = OutputMqttConfig.class, name = "OUTPUT_MQTT"),
    @JsonSubTypes.Type(value = OutputCommandConfig.class, name = "OUTPUT_COMMAND")
})
@Schema(description = "节点配置基类")
public abstract class NodeConfig implements Serializable {

    private static final long serialVersionUID = 1L;
}
