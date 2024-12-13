package com.github.dingdaoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.github.dingdaoyi.proto.model.DataTypeEnum;
import com.github.dingdaoyi.proto.model.ParamType;
import com.github.dingdaoyi.proto.model.KeyValue;
import com.github.dingdaoyi.proto.model.tsl.PropertyAccessMode;
import com.github.dingdaoyi.proto.model.tsl.TslProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.dreamlu.mica.core.utils.$;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dingyunwei
 */
@Schema
@Data
@TableName(value = "tb_model_property",autoResultMap = true)
public class ModelProperty {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "id")
    private Integer id;

    /**
     * 数据类型
     */
    @TableField(value = "data_type")
    @Schema(description = "数据类型")
    private DataTypeEnum dataType;

    /**
     * 备注
     */
    @TableField(value = "remark")
    @Schema(description = "备注")
    private String remark;

    /**
     * 标识符
     */
    @TableField(value = "identifier")
    @Schema(description = "标识符")
    private String identifier;

    /**
     * 属性名称
     */
    @TableField(value = "name")
    @Schema(description = "属性名称")
    private String name;

    /**
     * 访问权限:r,只读,rw读写
     */
    @TableField(value = "access_mode")
    @Schema(description = "访问权限:r,只读,rw读写")
    private PropertyAccessMode accessMode;

    /**
     * 产品类型
     */
    @TableField(value = "product_type_id")
    @Schema(description = "产品类型")
    private Integer productTypeId;

    /**
     * 图标
     */
    @TableField(value = "icon_id")
    @Schema(description = "图标")
    private Integer iconId;

    /**
     * 是否为自定义属性
     */
    @TableField(value = "custom")
    @Schema(description = "是否为自定义属性")
    private Boolean custom;

    /**
     * 布尔类型时,false对应的值
     */
    @TableField(value = "bool0")
    @Schema(description = "布尔类型时,false对应的值")
    private String bool0;

    /**
     * 布尔类型时,true对应的值
     */
    @TableField(value = "bool1")
    @Schema(description = "布尔类型时,true对应的值")
    private String bool1;

    /**
     * 字符串类型时,长度
     */
    @TableField(value = "length")
    @Schema(description = "字符串类型时,长度")
    private Integer length;

    /**
     * 参数类型,1属性, 2 参数(事件和服务的出入参数)
     */
    @TableField(value = "param_type")
    @Schema(description = "参数类型,1属性, 2 参数(事件和服务的出入参数)")
    private ParamType paramType;

    /**
     * 计量单位
     */
    @TableField(value = "unit")
    @Schema(description = "计量单位")
    private String unit;

    /**
     * 计量单位名称
     */
    @TableField(value = "unit_name")
    @Schema(description = "计量单位名称")
    private String unitName;

    /**
     * 最大值
     */
    @TableField(value = "max")
    @Schema(description = "最大值")
    private Long max;

    /**
     * 最小值
     */
    @TableField(value = "min")
    @Schema(description = "最小值")
    private Long min;

    /**
     * 步长
     */
    @TableField(value = "step")
    @Schema(description = "步长")
    private Long step;

    /**
     * 枚举参对照表
     */
    @TableField(value = "enums",typeHandler = JacksonTypeHandler.class)
    @Schema(description = "枚举参对照表")
    private List<KeyValue<Integer,String>> enums=new ArrayList<>();
    /**
     * 产品id
     */
    @TableField(value = "product_id")
    @Schema(description = "产品id")
    private Integer productId;


    @TableField(value = "parent_id")
    @Schema(description = "父级id,当数据类型为struct的时候会有,默认-1")
    private Integer parentId = -1;

    public TslProperty toTsl() {
        TslProperty tslProperty = new TslProperty();
        tslProperty.setId(this.getId());
        tslProperty.setDataType(this.getDataType());
        tslProperty.setRemark(this.getRemark());
        tslProperty.setIdentifier(this.getIdentifier());
        tslProperty.setName(this.getName());
        tslProperty.setAccessMode(this.getAccessMode());
        tslProperty.setBool0(this.getBool0());
        tslProperty.setBool1(this.getBool1());
        tslProperty.setLength(this.getLength());
        tslProperty.setUnit(this.getUnit());
        tslProperty.setUnitName(this.getUnitName());
        tslProperty.setMax(this.getMax());
        tslProperty.setMin(this.getMin());
        tslProperty.setStep(this.getStep());
        tslProperty.setEnums(this.getEnums());
        tslProperty.setParamType(this.getParamType());
        return tslProperty;
    }

    public String getUnitStr() {
        return $.isBlank(unit)?"":unit;
    }
}