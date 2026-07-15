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
@TableName("tb_firmware")
@Schema(description = "固件包")
public class Firmware extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("product_id")
    @Schema(description = "产品ID")
    private Integer productId;

    @Schema(description = "固件名称")
    private String name;

    @Schema(description = "版本号")
    private String version;

    @TableField("file_path")
    @Schema(description = "文件路径")
    private String filePath;

    @TableField("file_size")
    @Schema(description = "文件大小(字节)")
    private Long fileSize;

    @Schema(description = "校验和")
    private String checksum;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "状态: DRAFT/PUBLISHED/ARCHIVED")
    private String status;

    @TableField("created_by")
    private String createdBy;
}
