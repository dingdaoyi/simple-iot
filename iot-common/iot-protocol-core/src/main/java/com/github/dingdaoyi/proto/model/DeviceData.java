package com.github.dingdaoyi.proto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dingyunwei
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeviceData {
    private String identifier;
    private DataTypeEnum dataType;
    private Object value;
}
