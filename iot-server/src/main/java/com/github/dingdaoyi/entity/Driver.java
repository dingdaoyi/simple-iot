package com.github.dingdaoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.github.dingdaoyi.core.driver.DriverTypeEnum;
import com.github.dingdaoyi.model.enu.ConnectionTypeEnum;

/**
 * @author dingyunwei
 */
@Data
@TableName("tb_iot_driver")
@Schema(description = "驱动管理")
public class Driver {
    @TableId(type = IdType.AUTO)
    @Schema(description = "驱动ID")
    private Integer driverId;

    @Schema(description = "驱动名称")
    private String name;

    @Schema(description = "驱动类型")
    private DriverTypeEnum type;

    @Schema(description = "驱动描述")
    private String description;

    @Schema(description = "连接类型")
    private ConnectionTypeEnum connectionType;

    @Schema(description = "驱动JAR路径")
    private String jarPath;

    @Schema(description = "是否为默认驱动")
    private Boolean isDefault;

    @Schema(description = "状态 1启用 0禁用")
    private Integer status;

    @Schema(description = "驱动监听端口")
    private Integer port;
} 