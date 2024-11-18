package com.github.dingdaoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 品类
 * @author dingyunwei
 */
@Schema(description="产品类型")
@Data
@TableName(value = "tb_product_type")
public class ProductType {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description="id")
    private Integer id;

    /**
     * 产品类型名称
     */
    @TableField(value = "\"name\"")
    @Schema(description="产品类型名称")
    private String name;

    /**
     * 父级类型id
     */
    @TableField(value = "parent_id")
    @Schema(description="父级类型id")
    private Integer parentId;

    /**
     * 1,启用,2 禁用
     */
    @TableField(value = "\"status\"")
    @Schema(description="1,启用,2 禁用")
    private Integer status;

    /**
     * 图标路径
     */
    @TableField(value = "icon_url")
    @Schema(description="图标路径")
    private String iconUrl;

    /**
     * 描述
     */
    @TableField(value = "mark")
    @Schema(description="描述")
    private String mark;

    /**
     * 类型code,在多级别网关设备协议中使用
     */
    @TableField(value = "part_type_code")
    @Schema(description="类型code,在多级别网关设备协议中使用")
    private String partTypeCode;
}