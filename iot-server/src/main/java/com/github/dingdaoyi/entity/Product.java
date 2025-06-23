package com.github.dingdaoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.dingdaoyi.core.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingyunwei
 */
@Schema
@Data
@TableName(value = "tb_product")
public class Product  {
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "产品id")
    private Integer id;

    @TableField(value = "model")
    @Schema(description = "产品型号")
    private String model;

    @TableField(value = "manufacturer")
    @Schema(description = "厂家")
    private String manufacturer;

    @TableField(value = "remark")
    @Schema(description = "描述")
    private String remark;

    @TableField(value = "protocol_id")
    @Schema(description = "协议id")
    private Integer protocolId;

    @TableField(value = "product_type_id")
    @Schema(description = "产品类型id")
    private Integer productTypeId;

    @TableField(value = "product_key")
    @Schema(description = "产品接入key")
    private String productKey;
}