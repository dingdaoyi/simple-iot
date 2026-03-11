package com.github.dingdaoyi.rule.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 消息推送输出节点配置
 * @author dingyunwei
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "消息推送输出节点配置")
public class OutputMessageConfig extends NodeConfig {

    @Schema(description = "消息接收配置ID")
    private Integer messageReceiveId;

    @Schema(description = "消息标题模板，支持变量 ${deviceName} 等")
    private String title;

    @Schema(description = "消息内容模板，支持变量 ${deviceName} 等")
    private String content;
}
