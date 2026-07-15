package com.github.dingdaoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.dingdaoyi.core.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_modbus_config")
@Schema(description = "Modbus设备配置")
public class ModbusConfig extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("device_id")
    private Integer deviceId;

    @TableField("host")
    private String host;

    @TableField("port")
    private Integer port;

    @TableField("unit_id")
    private Short unitId;

    @TableField("interval_ms")
    private Integer intervalMs;

    @TableField("register_map")
    private String registerMap;

    @TableField("enabled")
    private Boolean enabled;
}
