package com.github.dingdaoyi.iot.model;

import java.io.Serializable;
import java.util.Map;

import cn.hutool.core.bean.BeanUtil;
import com.github.dingdaoyi.entity.DeviceCommand;
import com.github.dingdaoyi.proto.model.EncoderMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设备指令下发实体
 * @author dingyunwei
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommandRequest extends DeviceCommand {

    public CommandRequest(DeviceCommand command) {
        BeanUtil.copyProperties(command, this);
    }

    public EncoderMessage toMessage() {
        EncoderMessage message = new EncoderMessage();
        message.setDeviceKey(getDeviceKey());
        message.setIdentifier(getIdentifier());
        message.setParams(getInputParams());
        message.setProductKey(getProductKey());
        return message;
    }
}
