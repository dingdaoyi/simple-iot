package com.github.dingdaoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tb_device_group_relation")
public class DeviceGroupRelation {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("device_id")
    private Integer deviceId;

    @TableField("group_id")
    private Integer groupId;

    private LocalDateTime createTime;
}
