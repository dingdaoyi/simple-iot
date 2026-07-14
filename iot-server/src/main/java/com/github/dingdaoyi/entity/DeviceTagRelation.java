package com.github.dingdaoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tb_device_tag_relation")
public class DeviceTagRelation {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("device_id")
    private Integer deviceId;

    @TableField("tag_id")
    private Integer tagId;

    private LocalDateTime createTime;
}
