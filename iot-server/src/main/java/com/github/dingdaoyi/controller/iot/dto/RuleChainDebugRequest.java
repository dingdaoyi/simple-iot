package com.github.dingdaoyi.controller.iot.dto;

import com.github.dingdaoyi.entity.RuleChain;
import com.github.dingdaoyi.rule.RuleContext;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * 规则链草稿调试请求。
 *
 * @author dingyunwei
 */
@Data
@Schema(description = "规则链草稿调试请求")
public class RuleChainDebugRequest {

    @Valid
    @NotNull(message = "规则链不能为空")
    @Schema(description = "待调试的规则链草稿，支持未保存配置")
    private RuleChain ruleChain;

    @Schema(description = "设备Key")
    private String deviceKey;

    @Schema(description = "设备ID")
    private Integer deviceId;

    @Schema(description = "设备名称")
    private String deviceName;

    @NotNull(message = "消息类型不能为空")
    @Schema(description = "消息类型: PROPERTY/EVENT/SERVICE_RES/ONLINE/OFFLINE")
    private RuleContext.MessageType messageType;

    @Schema(description = "属性上报模拟数据，key为属性标识符，value为属性值")
    private Map<String, Object> properties;

    @Schema(description = "事件标识符")
    private String eventIdentifier;

    @Schema(description = "事件参数模拟数据，key为参数标识符，value为参数值")
    private Map<String, Object> eventParams;

    @Schema(description = "额外上下文变量，可用于模板和脚本调试")
    private Map<String, Object> enrichedData;
}
