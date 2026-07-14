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
@TableName("tb_device_tag")
@Schema(description = "设备标签")
public class DeviceTag extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @Schema(description = "标签名称")
    private String name;

    @TableField("color")
    @Schema(description = "标签颜色")
    private String color;
}
