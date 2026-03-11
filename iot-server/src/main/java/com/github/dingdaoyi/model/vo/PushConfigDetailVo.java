package com.github.dingdaoyi.model.vo;

import com.github.dingdaoyi.entity.PushConfig;
import com.github.dingdaoyi.entity.enu.PushConfigType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 推送配置详情VO
 * @author dingyunwei
 */
@Data
@Schema(description = "推送配置详情VO")
public class PushConfigDetailVo {

    @Schema(description = "配置ID")
    private Integer id;

    @Schema(description = "配置名称")
    private String name;

    @Schema(description = "配置类型")
    private PushConfigType type;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "是否启用")
    private Boolean isEnabled;

    // ==================== HTTP 配置 ====================

    @Schema(description = "HTTP请求URL")
    private String httpUrl;

    @Schema(description = "HTTP请求方法")
    private String httpMethod;

    @Schema(description = "HTTP请求头")
    private List<PushConfig.KeyValue> httpHeaders;

    @Schema(description = "超时时间(毫秒)")
    private Integer httpTimeout;

    // ==================== MQTT 配置 ====================

    @Schema(description = "MQTT Broker地址")
    private String mqttBroker;

    @Schema(description = "MQTT用户名")
    private String mqttUsername;

    @Schema(description = "MQTT密码")
    private String mqttPassword;

    @Schema(description = "MQTT客户端ID")
    private String mqttClientId;

    @Schema(description = "MQTT目标Topic")
    private String mqttTopic;

    @Schema(description = "MQTT QoS等级")
    private Integer mqttQos;

    @Schema(description = "MQTT保留消息")
    private Boolean mqttRetain;

    @Schema(description = "MQTT心跳间隔(秒)")
    private Integer mqttKeepAlive;

    @Schema(description = "MQTT清除会话")
    private Boolean mqttCleanSession;

    @Schema(description = "MQTT扩展配置")
    private List<PushConfig.KeyValue> mqttOptions;
}
