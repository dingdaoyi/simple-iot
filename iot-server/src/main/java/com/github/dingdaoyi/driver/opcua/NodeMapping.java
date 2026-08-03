package com.github.dingdaoyi.driver.opcua;

import com.github.dingdaoyi.proto.model.DataTypeEnum;

/**
 * OPC UA 节点映射项
 * ponytail: record, 一行搞定
 * @author dingyunwei
 */
public record NodeMapping(
    String identifier,
    String nodeId,
    String dataType,
    double scale
) {
    public DataTypeEnum toDataType() {
        if (scale != 1.0) return DataTypeEnum.DOUBLE;
        return switch (dataType.toLowerCase()) {
            case "int", "int16", "int32", "uint16", "uint32" -> DataTypeEnum.INT;
            case "float", "float32" -> DataTypeEnum.FLOAT;
            case "double", "float64" -> DataTypeEnum.DOUBLE;
            case "bool", "boolean" -> DataTypeEnum.BOOL;
            default -> DataTypeEnum.DOUBLE;
        };
    }
}
