package com.github.dingdaoyi.controller.iot.dto;

import com.github.dingdaoyi.entity.Protocol;
import com.github.dingdaoyi.proto.model.ProtoMessageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "协议脚本解码测试请求")
@Data
public class ProtocolDecodeTestRequest {

    @Schema(description = "待测试协议草稿")
    private Protocol protocol;

    @Schema(description = "设备Key")
    private String deviceKey;

    @Schema(description = "产品Key")
    private String productKey;

    @Schema(description = "消息类型")
    private ProtoMessageType messageType = ProtoMessageType.PROPERTY;

    @Schema(description = "原始报文，按 UTF-8 字符串传入")
    private String data;
}
