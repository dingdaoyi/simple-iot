package com.github.dingdaoyi.model.DTO;


import com.github.dingdaoyi.entity.Device;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingyunwei
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DeviceDTO extends Device {
    /**
     * 协议key
     */
    private String protoKey;
}
