package com.github.dingdaoyi.model.enu;

import io.swagger.v3.oas.annotations.media.Schema;
import net.dreamlu.mica.core.result.IResultCode;

/**
 * @author dyw
 */

@Schema(description  = "自定义")
public enum SysCodeEnum implements IResultCode {
    /**
     * 自定义code
     */
    UN_AUTHORIZED(401,"用户未登录"),
    UNAUTHORIZED(403,"无权限操作"),
    BAD_REQUEST(400,"请求参数错误"),
    NPE_ERROR(9999999,"数据异常"),
    ;
    private final int code;
    private  final String msg;

    SysCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    public static SysCodeEnum fromCode(int code) {
        for (SysCodeEnum sysCodeEnum : values()) {
            if (sysCodeEnum.code==code) {
                return sysCodeEnum;
            }
        }
        return SysCodeEnum.BAD_REQUEST;
    }
}
