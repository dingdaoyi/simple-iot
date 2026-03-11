package com.github.dingdaoyi.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.dingdaoyi.entity.enu.PushConfigType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 推送配置分页VO
 * @author dingyunwei
 */
@Data
@Schema(description = "推送配置分页VO")
public class PushConfigPageVo {

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

    // HTTP 配置摘要
    @Schema(description = "HTTP请求URL")
    private String httpUrl;

    @Schema(description = "HTTP请求方法")
    private String httpMethod;

    @Schema(description = "超时时间(毫秒)")
    private Integer httpTimeout;

    // MQTT 配置摘要
    @Schema(description = "MQTT Broker地址")
    private String mqttBroker;

    @Schema(description = "MQTT目标Topic")
    private String mqttTopic;

    @Schema(description = "MQTT QoS等级")
    private Integer mqttQos;

    @Schema(description = "MQTT保留消息")
    private Boolean mqttRetain;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
