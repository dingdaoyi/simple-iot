package com.github.dingdaoyi.proto.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author dingyunwei
 */
@Data
@AllArgsConstructor
public class DeviceData {

    /**
     * 标识符
     */
    private String identifier;

    /**
     * 数据类型
     */
    private DataTypeEnum dataType;

    /**
     * 数据值
     */
    private Object value;

}
