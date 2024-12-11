package com.github.dingdaoyi.model.query;

import com.github.dingdaoyi.entity.Device;
import com.github.dingdaoyi.model.ToEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author dingyunwei
 */
@Data
public class DeviceUpdateQuery implements ToEntity<Device> {

    /**
     * 产品id
     */
    @Schema(description = "id")
    @NotNull(message = "设备id不能为空")
    private Integer id;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    @NotNull(message = "设备名称不能为空")
    private String deviceName;


    @Override
    public Device toEntity() {
        Device device = new Device();
        device.setId(id);
        device.setDeviceName(deviceName);
        return device;
    }
}
