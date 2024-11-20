package com.github.dingdaoyi.entity.enu;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.dingdaoyi.iot.proto.ProtocolInitialize;
import com.github.dingdaoyi.iot.proto.impl.JavaProtocolInitialize;
import com.github.dingdaoyi.iot.proto.impl.ScriptProtocolInitialize;
import com.github.dingdaoyi.iot.proto.impl.SystemProtocolInitialize;
import lombok.Getter;

/**
 * @author dingyunwei
 */

@Getter
public enum ProtoType {
    JAVA(1, JavaProtocolInitialize.class),
    /**
     * 系统默认内部
     */
    SYSTEM_DEFAULT(2, SystemProtocolInitialize.class),
    JAVASCRIPT(3, ScriptProtocolInitialize.class);

    @EnumValue
    @JsonValue
    private final int code;
    private final Class<? extends ProtocolInitialize> initClass;

    ProtoType(int code, Class<? extends ProtocolInitialize> initClass) {
        this.code = code;
        this.initClass = initClass;
    }
}
