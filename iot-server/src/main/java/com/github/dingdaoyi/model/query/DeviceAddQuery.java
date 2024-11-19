package com.github.dingdaoyi.model.query;

import com.baomidou.mybatisplus.annotation.TableField;
import com.github.dingdaoyi.entity.Device;
import com.github.dingdaoyi.entity.Product;
import com.github.dingdaoyi.model.ToEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author dingyunwei
 */
@Data
public class DeviceAddQuery implements ToEntity<Device> {

    /**
     * 产品id
     */
    @Schema(description = "产品id")
    @NotNull(message = "产品不能为空")
    private Integer productId;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    @NotNull(message = "设备名称不能为空")
    private String deviceName;

    /**
     * 设备key
     */
    @Schema(description = "设备key")
    @NotNull(message = "设备号不能为空")
    private String deviceKey;


    @Override
    public Device toEntity() {
        Device device = new Device();
        device.setProductId(productId);
        device.setDeviceName(deviceName);
        device.setDeviceKey(deviceKey);
        //自定义串
        device.setDeviceSecret(RandomStringUtils.randomAlphanumeric(20));
        return device;
    }
}
