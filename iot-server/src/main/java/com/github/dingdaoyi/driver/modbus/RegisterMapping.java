package com.github.dingdaoyi.driver.modbus;

import com.github.dingdaoyi.proto.model.DataTypeEnum;

/**
 * Modbus 寄存器映射项
 * ponytail: 用 record，一行搞定
 */
public record RegisterMapping(
    String identifier,
    int function,
    int address,
    int count,
    String dataType,
    double scale
) {
    public DataTypeEnum toDataType() {
        // ponytail: any type with scale != 1.0 produces fractional values, must be DOUBLE
        if (scale != 1.0) return DataTypeEnum.DOUBLE;
        return switch (dataType.toLowerCase()) {
            case "int", "int16", "int32" -> DataTypeEnum.INT;
            case "float", "float32" -> DataTypeEnum.FLOAT;
            case "double", "float64" -> DataTypeEnum.DOUBLE;
            case "bool" -> DataTypeEnum.BOOL;
            default -> DataTypeEnum.DOUBLE;
        };
    }
}
