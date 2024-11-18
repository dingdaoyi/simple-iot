package com.github.dingdaoyi.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务关联属性表
 *
 * @author dingyunwei
 */
@Schema(description = "服务关联属性表")
@Data
@TableName(value = "tb_service_property")
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProperty {
    public static final int INPUT_TYPE = 2;
    public static final int OUTPUT_TYPE = 1;
    /**
     * 服务id
     */
    @TableField(value = "service_id")
    @Schema(description = "服务id")
    private Integer serviceId;

    /**
     * 属性id
     */
    @TableField(value = "property_id")
    @Schema(description = "属性id")
    private Integer propertyId;

    /**
     * 1:出参数,2:入参
     */
    @TableField(value = "param_type")
    @Schema(description = "1:出参数,2:入参")
    private Integer paramType;
}