package com.github.dingdaoyi.proto.model;

import lombok.Data;

import java.util.Map;

/**
 * @author dwx
 */
@Data
public class EncoderResult {
    /**
     * 消息id
     */
    private Integer messageId;

    /**
     * 是否需要回复
     */
    private boolean needReply = true;
    /**
     * 编码后的消息体
     */
    private byte[] message;

    /**
     * 指令下发的元数据, 不同的协议可能需要的元数据不同,需要通过协议返回
     */
   private Map<String, Object> metadata;
}
