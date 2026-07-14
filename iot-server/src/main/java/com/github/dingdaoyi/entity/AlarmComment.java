package com.github.dingdaoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tb_alarm_comment")
@Schema(description = "告警评论")
public class AlarmComment {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("alarm_id")
    @Schema(description = "告警ID")
    private Integer alarmId;

    @Schema(description = "评论内容")
    private String comment;

    @Schema(description = "操作类型: COMMENT, ACKNOWLEDGE, CLEAR, ESCALATE")
    private String action;

    @Schema(description = "操作人")
    private String author;

    private LocalDateTime createTime;
}
