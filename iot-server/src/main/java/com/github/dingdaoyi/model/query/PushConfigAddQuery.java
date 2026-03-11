package com.github.dingdaoyi.model.query;

import com.github.dingdaoyi.entity.PushConfig;
import com.github.dingdaoyi.entity.enu.PushConfigType;
import com.github.dingdaoyi.model.ToEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 推送配置新增请求
 * @author dingyunwei
 */
@Data
@Schema(description = "推送配置新增请求")
public class PushConfigAddQuery implements ToEntity<PushConfig> {

    @Schema(description = "配置名称")
    @NotBlank(message = "配置名称不能为空")
    private String name;

    @Schema(description = "配置类型")
    @NotNull(message = "配置类型不能为空")
    private PushConfigType type;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "是否启用")
    private Boolean isEnabled = true;

    // ==================== HTTP 配置 ====================

    @Schema(description = "HTTP请求URL")
    private String httpUrl;

    @Schema(description = "HTTP请求方法: GET/POST/PUT")
    private String httpMethod = "POST";

    @Schema(description = "HTTP请求头")
    private List<PushConfig.KeyValue> httpHeaders;

    @Schema(description = "超时时间(毫秒)")
    private Integer httpTimeout = 5000;

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

    @Schema(description = "MQTT QoS等级: 0/1/2")
    private Integer mqttQos = 0;

    @Schema(description = "MQTT保留消息")
    private Boolean mqttRetain = false;

    @Schema(description = "MQTT心跳间隔(秒)")
    private Integer mqttKeepAlive = 60;

    @Schema(description = "MQTT清除会话")
    private Boolean mqttCleanSession = true;

    @Schema(description = "MQTT扩展配置")
    private List<PushConfig.KeyValue> mqttOptions;

    @Override
    public PushConfig toEntity() {
        PushConfig config = new PushConfig();
        config.setName(name);
        config.setType(type);
        config.setDescription(description);
        config.setIsEnabled(isEnabled);

        // HTTP 配置
        config.setHttpUrl(httpUrl);
        config.setHttpMethod(httpMethod);
        config.setHttpHeaders(httpHeaders);
        config.setHttpTimeout(httpTimeout);

        // MQTT 配置
        config.setMqttBroker(mqttBroker);
        config.setMqttUsername(mqttUsername);
        config.setMqttPassword(mqttPassword);
        config.setMqttClientId(mqttClientId);
        config.setMqttTopic(mqttTopic);
        config.setMqttQos(mqttQos);
        config.setMqttRetain(mqttRetain);
        config.setMqttKeepAlive(mqttKeepAlive);
        config.setMqttCleanSession(mqttCleanSession);
        config.setMqttOptions(mqttOptions);

        return config;
    }
}
