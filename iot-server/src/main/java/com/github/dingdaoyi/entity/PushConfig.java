package com.github.dingdaoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.github.dingdaoyi.core.base.BaseEntity;
import com.github.dingdaoyi.entity.enu.PushConfigType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 推送配置实体
 * @author dingyunwei
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "tb_push_config", autoResultMap = true)
@Schema(description = "推送配置")
public class PushConfig extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "配置ID")
    private Integer id;

    @TableField(value = "name")
    @Schema(description = "配置名称")
    private String name;

    @TableField(value = "type")
    @Schema(description = "配置类型: HTTP/MQTT")
    private PushConfigType type;

    @TableField(value = "description")
    @Schema(description = "描述")
    private String description;

    @TableField(value = "is_enabled")
    @Schema(description = "是否启用")
    private Boolean isEnabled;

    // ==================== HTTP 配置 ====================

    @TableField(value = "http_url")
    @Schema(description = "HTTP请求URL")
    private String httpUrl;

    @TableField(value = "http_method")
    @Schema(description = "HTTP请求方法: GET/POST/PUT")
    private String httpMethod;

    @TableField(value = "http_headers", typeHandler = JacksonTypeHandler.class)
    @Schema(description = "HTTP请求头(JSON数组)")
    private List<KeyValue> httpHeaders;

    @TableField(value = "http_timeout")
    @Schema(description = "超时时间(毫秒)")
    private Integer httpTimeout;

    // ==================== MQTT 配置 ====================

    @TableField(value = "mqtt_broker")
    @Schema(description = "MQTT Broker地址 (如: tcp://localhost:1883)")
    private String mqttBroker;

    @TableField(value = "mqtt_username")
    @Schema(description = "MQTT用户名")
    private String mqttUsername;

    @TableField(value = "mqtt_password")
    @Schema(description = "MQTT密码")
    private String mqttPassword;

    @TableField(value = "mqtt_client_id")
    @Schema(description = "MQTT客户端ID")
    private String mqttClientId;

    @TableField(value = "mqtt_topic")
    @Schema(description = "MQTT目标Topic")
    private String mqttTopic;

    @TableField(value = "mqtt_qos")
    @Schema(description = "MQTT QoS等级: 0/1/2")
    private Integer mqttQos;

    @TableField(value = "mqtt_retain")
    @Schema(description = "MQTT保留消息")
    private Boolean mqttRetain;

    @TableField(value = "mqtt_keep_alive")
    @Schema(description = "MQTT心跳间隔(秒)")
    private Integer mqttKeepAlive;

    @TableField(value = "mqtt_clean_session")
    @Schema(description = "MQTT清除会话")
    private Boolean mqttCleanSession;

    @TableField(value = "mqtt_options", typeHandler = JacksonTypeHandler.class)
    @Schema(description = "MQTT扩展配置(JSON数组)")
    private List<KeyValue> mqttOptions;

    /**
     * 键值对配置项 (用于headers/options等)
     */
    @Data
    public static class KeyValue {
        @Schema(description = "键")
        private String key;
        @Schema(description = "值")
        private String value;
    }
}
