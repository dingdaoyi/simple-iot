package com.github.dingdaoyi.rule.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 脚本过滤节点配置
 * @author dingyunwei
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "脚本过滤节点配置")
public class FilterScriptConfig extends NodeConfig {

    @Schema(description = "脚本语言: javascript, groovy")
    private String language = "javascript";

    @Schema(description = "脚本内容，返回 true/false")
    private String script;
}
