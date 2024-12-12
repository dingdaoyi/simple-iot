package com.github.dingdaoyi.proto.model.tsl;

import com.github.dingdaoyi.proto.model.DataTypeEnum;
import com.github.dingdaoyi.proto.model.KeyValue;
import com.github.dingdaoyi.proto.model.ParamType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author dingyunwei
 */
@Schema(description = "属性")
@Data
public class TslProperty {
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
    private List<KeyValue<Integer, String>> enums;

    /**
     * 子级
     */
    private List<TslProperty> children;

    /**
     * 参数类型,1属性, 2 参数(事件和服务的出入参数)
     */
    @Schema(description = "数类型,1属性, 2 参数(事件和服务的出入参数)")
    private ParamType paramType;

    public Object parsePropertyValue(Object value) {
        return this.dataType.parse(value, this);
    }

    public boolean validType(Object value) {
        return this.dataType.validType(value);
    }
    public boolean isProperty() {
        return paramType==ParamType.PROPERTY;
    }
}