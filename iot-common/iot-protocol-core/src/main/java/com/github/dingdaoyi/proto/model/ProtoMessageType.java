package com.github.dingdaoyi.proto.model;

import java.util.Arrays;
import java.util.Optional;

/**
 * 协议数据类型
 *
 * @author dingyunwei
 */

public enum ProtoMessageType {
    /**
     * 事件
     */
    EVENT("ev"),
    /**
     * 属性上报
     */
    PROPERTY("pro"),
    /**
     * 服务响应
     */
    SERVICE_RES("cam_res"),
    ;
    public final String code;

    ProtoMessageType(String code) {
        this.code = code;
    }
    public static Optional<ProtoMessageType> fromCode(String code) {
        return Arrays.stream(values())
                .filter(item->item.code.equals(code))
                .findAny();
    }
}
