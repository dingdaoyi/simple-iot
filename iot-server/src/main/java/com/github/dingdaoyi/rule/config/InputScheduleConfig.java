package com.github.dingdaoyi.rule.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 定时触发输入节点配置
 * @author dingyunwei
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "定时触发输入节点配置")
public class InputScheduleConfig extends NodeConfig {

    @Schema(description = "cron 表达式 (6位: 秒 分 时 日 月 周)")
    private String cron;

    @Schema(description = "触发时设备Key (可选, 用于上下文)")
    private String deviceKey;

    @Schema(description = "时区, 默认系统时区")
    private String timezone;
}
