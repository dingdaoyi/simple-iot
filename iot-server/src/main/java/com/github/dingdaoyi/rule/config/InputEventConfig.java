package com.github.dingdaoyi.rule.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 事件上报输入节点配置
 * @author dingyunwei
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "事件上报输入节点配置")
public class InputEventConfig extends NodeConfig {

    @Schema(description = "要监听的事件标识符列表，为空则监听所有事件")
    private List<String> identifiers;
}
