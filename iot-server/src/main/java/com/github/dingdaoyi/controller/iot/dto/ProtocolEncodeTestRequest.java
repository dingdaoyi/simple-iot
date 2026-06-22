package com.github.dingdaoyi.controller.iot.dto;

import com.github.dingdaoyi.entity.Protocol;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Schema(description = "协议脚本编码测试请求")
@Data
public class ProtocolEncodeTestRequest {

    @Schema(description = "待测试协议草稿")
    private Protocol protocol;

    @Schema(description = "设备Key")
    private String deviceKey;

    @Schema(description = "产品Key")
    private String productKey;

    @Schema(description = "功能/属性标识符")
    private String identifier;

    @Schema(description = "下行参数")
    private Map<String, Object> params = new HashMap<>();
}
