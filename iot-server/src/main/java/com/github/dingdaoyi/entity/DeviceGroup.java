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
@TableName("tb_device_group")
@Schema(description = "设备分组")
public class DeviceGroup extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("parent_id")
    @Schema(description = "父分组ID, -1=根")
    private Integer parentId;

    @Schema(description = "分组名称")
    private String name;

    @Schema(description = "描述")
    private String description;
}
