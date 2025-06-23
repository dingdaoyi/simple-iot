package com.github.dingdaoyi.core.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.dingdaoyi.core.enums.ResultCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 统一响应结果
 *
 * @author dingyunwei
 */
@Data
@Schema(description = "统一响应结果")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResult<T> {

    @Schema(description = "响应码")
    private Integer code;

    @Schema(description = "响应消息")
    private String msg;

    @Schema(description = "响应数据")
    private T data;

    @Schema(description = "是否成功")
    private boolean success;

    private BaseResult(Integer code, String msg, T data, boolean success) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.success = success;
    }

    public static <T> BaseResult<T> success() {
        return new BaseResult<>(ResultCode.SUCCESS.getCode(), "操作成功", null, true);
    }

    public static <T> BaseResult<T> success(T data) {
        return new BaseResult<>(ResultCode.SUCCESS.getCode(), "操作成功", data, true);
    }

    public static <T> BaseResult<T> success(String msg, T data) {
        return new BaseResult<>(ResultCode.SUCCESS.getCode(), msg, data, true);
    }

    public static <T> BaseResult<T> fail(String msg) {
        return new BaseResult<>(ResultCode.INTERNAL_SERVER_ERROR.getCode(), msg, null, false);
    }

    public static <T> BaseResult<T> fail(ResultCode code, String msg) {
        return new BaseResult<>(code.getCode(), msg, null, false);
    }

    public static <T> BaseResult<T> fail(Integer code, String msg) {
        return new BaseResult<>(code, msg, null, false);
    }
}