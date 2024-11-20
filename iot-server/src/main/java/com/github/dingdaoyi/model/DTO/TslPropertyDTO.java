package com.github.dingdaoyi.model.DTO;

import com.github.dingdaoyi.entity.ModelProperty;
import com.github.dingdaoyi.proto.model.DataTypeEnum;
import com.github.dingdaoyi.entity.enu.PropertyAccessMode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.HashMap;
import java.util.List;

/**
 * @author dingyunwei
 */
@Schema(description = "属性")
@Data
public class TslPropertyDTO {
    /**
     * id
     */
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
     * 标识符
     */
    @Schema(description = "标识符")
    private String identifier;

    /**
     * 属性名称
     */
    @Schema(description = "属性名称")
    private String name;

    /**
     * 访问权限:r,只读,rw读写
     */
    @Schema(description = "访问权限:r,只读,rw读写")
    private PropertyAccessMode accessMode;

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
    private HashMap<Integer, String> enumMap;

    /**
     * 子级
     */
    private List<TslPropertyDTO> children;

    public static TslPropertyDTO of(ModelProperty property) {
        TslPropertyDTO propertyDTO = new TslPropertyDTO();
        propertyDTO.setId(property.getId());
        propertyDTO.setDataType(property.getDataType());
        propertyDTO.setMark(property.getMark());
        propertyDTO.setIdentifier(property.getIdentifier());
        propertyDTO.setName(property.getName());
        propertyDTO.setAccessMode(property.getAccessMode());
        propertyDTO.setBool0(property.getBool0());
        propertyDTO.setBool1(property.getBool1());
        propertyDTO.setLength(property.getLength());
        propertyDTO.setUnit(property.getUnit());
        propertyDTO.setUnitName(property.getUnitName());
        propertyDTO.setMax(property.getMax());
        propertyDTO.setMin(property.getMin());
        propertyDTO.setStep(property.getStep());
        propertyDTO.setEnumMap(property.getEnumMap());
        return propertyDTO;
    }
}