package com.github.dingdaoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author dingyunwei
 */
@Schema
@Data
@TableName(value = "tb_product")
public class Product {
    /**
     * 产品id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "产品id")
    private Integer id;

    /**
     * 产品型号
     */
    @TableField(value = "model")
    @Schema(description = "产品型号")
    private String model;

    /**
     * 厂家
     */
    @TableField(value = "manufacturer")
    @Schema(description = "厂家")
    private String manufacturer;

    /**
     * 描述
     */
    @TableField(value = "mark")
    @Schema(description = "描述")
    private String mark;

    /**
     * 协议id
     */
    @TableField(value = "protocol_id")
    @Schema(description = "协议id")
    private Integer protocolId;

    @TableField(value = "product_type_id")
    @Schema(description = "产品类型id")
    private Integer productTypeId;
}