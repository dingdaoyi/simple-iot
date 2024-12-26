package com.github.dingdaoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.dingdaoyi.model.enu.NotifyType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author dingyunwei
 */
@Schema
@Data
@TableName(value = "tb_message_receive")
public class MessageReceive {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "id")
    private Integer id;

    /**
     * 接收者名称
     */
    @TableField(value = "name")
    @Schema(description = "接收者名称")
    private String name;

    /**
     * 接收对象
     */
    @TableField(value = "receiver")
    @Schema(description = "接收对象")
    private String receiver;

    /**
     * 通知类型
     */
    @TableField(value = "notify_type")
    @Schema(description = "通知类型")
    private NotifyType notifyType;

    /**
     * 备注
     */
    @TableField(value = "remark")
    @Schema(description = "备注")
    private String remark;
}