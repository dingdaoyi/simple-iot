package com.github.dingdaoyi.controller.iot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 推送配置测试请求。
 */
@Data
@Schema(description = "推送配置测试请求")
public class PushTestRequest {

    @Schema(description = "事件类型，会写入 X-Simple-IoT-Event 请求头")
    private String eventType = "push.test";

    @Schema(description = "测试推送负载。可以是 JSON 字符串、普通字符串或对象")
    private Object payload;
}
