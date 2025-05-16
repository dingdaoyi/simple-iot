package com.github.dingdaoyi.model.vo;

import cn.hutool.core.bean.BeanUtil;
import com.github.dingdaoyi.entity.ModelProperty;
import com.github.dingdaoyi.proto.model.DataTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.stream.Collectors;

/**
 * @author dingyunwei
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ModelPropertyVo extends ModelProperty {

    public ModelPropertyVo(ModelProperty modelProperty) {
        BeanUtil.copyProperties(modelProperty, this);
        this.definitions = parseDataDefinitions(modelProperty.getDataType(), modelProperty);
    }

    private String parseDataDefinitions(DataTypeEnum dataType, ModelProperty modelProperty) {
        return switch (dataType) {
            case INT, DOUBLE, FLOAT -> String.format("取值范围 :%d-%d%s", modelProperty.getMin(), modelProperty.getMax(),
                    modelProperty.getUnitStr());
            case TEXT -> String.format("长度:%d", modelProperty.getLength());
            case ENUM -> "枚举值 :" + modelProperty.getEnums()
                    .stream()
                    .map(entry -> String.format("%d--%s", entry.getKey(), entry.getValue()))
                    .collect(Collectors.joining(" "));
            case BOOL -> String.format("false:%s true:%s",modelProperty.getBool0(),modelProperty.getBool1());
            case STRUCT -> "结构体";
            case DATE -> "日期格式";
        };
    }

    /**
     * 数据定义
     */
    private String definitions;

    /**
     * 名称
     *
     * @return
     */
    public String getDataTypeName() {
        return super.getDataType().getName();
    }

}
