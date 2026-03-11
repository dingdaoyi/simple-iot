package com.github.dingdaoyi.rule.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 清除告警节点配置
 * @author dingyunwei
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "清除告警节点配置")
public class AlarmClearConfig extends NodeConfig {

    @Schema(description = "告警类型，为空则清除所有活动告警")
    private String alarmType;
}
