package com.github.dingdaoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 用字典表
 * @author dingyunwei
 */
@Schema(description="用字典表")
@Data
@TableName(value = "tb_dict")
public class Dict {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description="主键")
    private Long id;

    /**
     * 字典分组
     */
    @TableField(value = "dict_group")
    @Schema(description="字典分组")
    private String dictGroup;

    /**
     * 字典描述
     */
    @TableField(value = "dict_label")
    @Schema(description="字典键")
    private String label;

    /**
     * 字典值
     */
    @TableField(value = "dict_value")
    @Schema(description="字典值")
    private String value;

    /**
     * 备注信息
     */
    @TableField(value = "remark")
    @Schema(description="备注信息")
    private String remark;

    /**
     * 排序字段
     */
    @TableField(value = "sort")
    @Schema(description="排序字段")
    private Integer sort;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @Schema(description="创建时间")
    private LocalDateTime createTime;

    /**
     * 创建时间
     */
    @TableField(value = "update_time")
    @Schema(description="创建时间")
    private LocalDateTime updateTime;
}