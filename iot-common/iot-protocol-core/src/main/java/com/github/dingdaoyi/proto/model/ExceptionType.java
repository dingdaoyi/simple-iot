package com.github.dingdaoyi.proto.model;


/**
 * @author dingyunwei
 */

public enum ExceptionType {
    PRODUCT_NOT_FOUND(2001,"产品key不正确,忽略解析"),
    TSL_MODEL_NOT_CONFIG(2002,"未配置物模型,无法解析"),
    INVALID_PARAM(2003,"参数错误,无法解析"),
    NULL_PARAM(2004,"参数值为空"),
    NULL_CONFIGURED_PARAMETER(2005,"参数未配置对应物模型"),
    ;

    public final int code;
    public final String msg;

    ExceptionType(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
