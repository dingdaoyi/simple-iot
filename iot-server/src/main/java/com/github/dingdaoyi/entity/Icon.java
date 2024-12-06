package com.github.dingdaoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema
@Data
@TableName(value = "tb_icon")
public class Icon {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "id")
    private Integer id;

    /**
     * 名称备注
     */
    @TableField(value = "name")
    @Schema(description = "名称备注")
    @NotBlank(message = "名称不能为空")
    private String name;

    /**
     * 地址
     */
    @TableField(value = "path")
    @Schema(description = "地址")
    @NotBlank(message = "文件路径不能为空")
    private String path;
}