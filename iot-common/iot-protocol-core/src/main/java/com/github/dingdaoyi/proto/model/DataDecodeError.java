package com.github.dingdaoyi.proto.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DataDecodeError {

    /**
     * 标识符
     */
    private String identifier;

    /**
     * 错误信息,传递物模型名称
     */
    private String message;

    /**
     * 告警值
     */
    private Object value;

    /**
     * 错误信息分类
     */
    private DecodeErrorType errorType;

}
