package com.github.dingdaoyi.rule;

import com.github.dingdaoyi.proto.model.DecodeResult;
import com.github.dingdaoyi.proto.model.DeviceData;
import com.github.dingdaoyi.proto.model.tsl.TslModel;
import com.github.dingdaoyi.proto.model.tsl.TslProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 规则执行上下文 - 基于物模型
 * @author dingyunwei
 */
@Data
public class RuleContext {

    // ===== 设备信息 =====
    private String deviceKey;
    private Integer deviceId;
    private String deviceName;

    // ===== 物模型 =====
    private TslModel tslModel;

    // ===== 解析后的数据 =====
    private DecodeResult decodeResult;

    // ===== 消息类型 =====
    private MessageType messageType;

    // ===== 事件时间 =====
    private LocalDateTime eventTime = LocalDateTime.now();

    // ===== 规则链ID =====
    private Integer ruleChainId;

    // ===== 丰富数据 (节点可添加) =====
    private Map<String, Object> enrichedData = new HashMap<>();

    // ===== 执行轨迹 (调试) =====
    private List<ExecutionTrace> traces = new ArrayList<>();

    /**
     * 消息类型
     */
    public enum MessageType {
        PROPERTY,    // 属性上报
        EVENT,       // 事件上报
        SERVICE_RES, // 服务响应
        ONLINE,      // 设备上线
        OFFLINE      // 设备下线
    }

    // ===== 便捷方法 =====

    /**
     * 获取属性值 (通过标识符)
     */
    public Optional<DeviceData> getProperty(String identifier) {
        if (decodeResult == null || decodeResult.getDataList() == null) {
            return Optional.empty();
        }
        return decodeResult.getDataList().stream()
            .filter(d -> d.getIdentifier().equals(identifier))
            .findFirst();
    }

    /**
     * 获取属性值
     */
    public Object getPropertyValue(String identifier) {
        return getProperty(identifier).map(DeviceData::getValue).orElse(null);
    }

    /**
     * 获取物模型属性定义
     */
    public Optional<TslProperty> getTslProperty(String identifier) {
        if (tslModel == null) {
            return Optional.empty();
        }
        return tslModel.propertyByIdentifier(identifier);
    }

    /**
     * 获取所有属性数据
     */
    public List<DeviceData> getAllProperties() {
        if (decodeResult == null || decodeResult.getDataList() == null) {
            return Collections.emptyList();
        }
        return decodeResult.getDataList();
    }

    /**
     * 获取事件数据
     */
    public Optional<com.github.dingdaoyi.proto.model.DeviceEventData> getEventData() {
        return Optional.ofNullable(decodeResult)
            .map(DecodeResult::getEventData);
    }

    /**
     * 是否是属性上报
     */
    public boolean isPropertyMessage() {
        return messageType == MessageType.PROPERTY;
    }

    /**
     * 是否是事件上报
     */
    public boolean isEventMessage() {
        return messageType == MessageType.EVENT;
    }

    /**
     * 添加执行轨迹
     */
    public void addTrace(String nodeId, String nodeName, String connectionType, String detail) {
        traces.add(new ExecutionTrace(nodeId, nodeName, connectionType, detail, LocalDateTime.now()));
    }

    /**
     * 构建变量映射 (用于模板替换)
     */
    public Map<String, Object> buildVariables() {
        Map<String, Object> vars = new HashMap<>();
        vars.put("deviceKey", deviceKey);
        vars.put("deviceName", deviceName);
        vars.put("deviceId", deviceId);
        vars.put("eventTime", eventTime);

        // 添加所有属性值
        if (decodeResult != null && decodeResult.getDataList() != null) {
            for (DeviceData data : decodeResult.getDataList()) {
                vars.put(data.getIdentifier(), data.getValue());
            }
        }

        // 添加丰富数据
        vars.putAll(enrichedData);

        return vars;
    }

    @Data
    public static class ExecutionTrace {
        private String nodeId;
        private String nodeName;
        private String connectionType;
        private String detail;
        private LocalDateTime timestamp;

        public ExecutionTrace(String nodeId, String nodeName, String connectionType, String detail, LocalDateTime timestamp) {
            this.nodeId = nodeId;
            this.nodeName = nodeName;
            this.connectionType = connectionType;
            this.detail = detail;
            this.timestamp = timestamp;
        }
    }
}
