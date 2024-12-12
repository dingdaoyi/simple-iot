package com.github.dingdaoyi.proto.model;

/**
 * @param identifier 标识符
 * @param dataType   数据类型
 * @param value      数据值
 * @author dingyunwei
 */
public record DeviceData(String identifier, DataTypeEnum dataType, Object value) {

}
