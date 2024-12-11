package com.github.dingdaoyi.model.query;

import com.github.dingdaoyi.entity.ModelProperty;
import com.github.dingdaoyi.proto.model.DataTypeEnum;
import com.github.dingdaoyi.proto.model.ParamType;
import com.github.dingdaoyi.proto.model.KeyValue;
import com.github.dingdaoyi.proto.model.tsl.PropertyAccessMode;
import com.github.dingdaoyi.model.ToEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author dingyunwei
 */
@Data
public class ModelPropertyUpdateQuery implements ToEntity<ModelProperty> {

    @Schema(description = "id")
    private Integer id;

    /**
     * 数据类型
     */
    @Schema(description = "数据类型")
    private DataTypeEnum dataType;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String mark;


    /**
     * 访问权限:r,只读,rw读写
     */
    @Schema(description = "访问权限:r,只读,rw读写")
    private PropertyAccessMode accessMode;

    /**
     * 图标
     */
    @Schema(description = "图标")
    private Integer iconId;


    /**
     * 布尔类型时,false对应的值
     */
    @Schema(description = "布尔类型时,false对应的值")
    private String bool0;

    /**
     * 布尔类型时,true对应的值
     */
    @Schema(description = "布尔类型时,true对应的值")
    private String bool1;

    /**
     * 字符串类型时,长度
     */
    @Schema(description = "字符串类型时,长度")
    private Integer length;

    /**
     * 参数类型,1属性, 2 参数(事件和服务的出入参数)
     */
    @Schema(description = "参数类型,1属性, 2 参数(事件和服务的出入参数)")
    private ParamType paramType = ParamType.PROPERTY;

    /**
     * 计量单位
     */
    @Schema(description = "计量单位")
    private String unit;

    /**
     * 计量单位名称
     */
    @Schema(description = "计量单位名称")
    private String unitName;

    /**
     * 最大值
     */
    @Schema(description = "最大值")
    private Long max;

    /**
     * 最小值
     */
    @Schema(description = "最小值")
    private Long min;

    /**
     * 步长
     */
    @Schema(description = "步长")
    private Long step;

    /**
     * 枚举参对照表
     */
    @Schema(description = "枚举参对照表")
    private List<KeyValue<Integer,String>> enums;

    @Override
    public ModelProperty toEntity() {
        ModelProperty modelProperty = new ModelProperty();
        modelProperty.setDataType(dataType);
        modelProperty.setMark(mark);
        modelProperty.setAccessMode(accessMode);
        modelProperty.setIconId(iconId);
        modelProperty.setCustom(false);
        modelProperty.setBool0(bool0);
        modelProperty.setBool1(bool1);
        modelProperty.setLength(length);
        modelProperty.setParamType(paramType);
        modelProperty.setUnit(unit);
        modelProperty.setUnitName(unitName);
        modelProperty.setMax(max);
        modelProperty.setMin(min);
        modelProperty.setStep(step);
        modelProperty.setEnums(enums);
        modelProperty.setId(id);
        return modelProperty;
    }
}
