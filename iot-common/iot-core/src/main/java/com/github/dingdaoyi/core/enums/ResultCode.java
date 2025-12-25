package com.github.dingdaoyi.core.enums;


/**
 * 响应状态码枚举
 *
 * @author dingyunwei
 */
public enum ResultCode {

    SUCCESS(200, "成功"),

    // ========== 客户端错误段 ==========

    BAD_REQUEST(400, "请求参数不正确"),
    UNAUTHORIZED(401, "账号未登录"),
    FORBIDDEN(403, "没有该操作权限"),
    NOT_FOUND(404, "请求未找到"),
    METHOD_NOT_ALLOWED(405, "请求方法不正确"),
    LOCKED(423, "请求失败，请稍后重试"),
    TOO_MANY_REQUESTS(429, "请求过于频繁，请稍后重试"),

    // ========== 服务端错误段 ==========

    INTERNAL_SERVER_ERROR(500, "系统异常"),
    NOT_IMPLEMENTED(501, "功能未实现/未开启"),
    ERROR_CONFIGURATION(502, "错误的配置项"),

    // ========== 自定义错误段 ==========
    REPEATED_REQUESTS(900, "重复请求，请稍后重试"),
    DEMO_DENY(901, "演示模式，禁止写操作"),
    PARAM_VALID_ERROR(902, "参数错误"),
    NPE_ERROR(903, "空指针异常"),
    PROTO_NOT_EXIST(904, "协议不存在"),

    UNKNOWN(999, "未知错误"),
    ;

    private final Integer code;

    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }

    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
