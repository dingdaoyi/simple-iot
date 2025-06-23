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
    private String message;

    @Schema(description = "响应数据")
    private T data;

    @Schema(description = "时间戳")
    private Long timestamp;

    public BaseResult() {
        this.timestamp = System.currentTimeMillis();
    }

    public BaseResult(Integer code, String message) {
        this();
        this.code = code;
        this.message = message;
    }

    public BaseResult(Integer code, String message, T data) {
        this(code, message);
        this.data = data;
    }

    public static <T> BaseResult<T> success() {
        return new BaseResult<>(200, "操作成功");
    }

    public static <T> BaseResult<T> success(T data) {
        return new BaseResult<>(200, "操作成功", data);
    }

    public static <T> BaseResult<T> success(String message, T data) {
        return new BaseResult<>(200, message, data);
    }

    public static <T> BaseResult<T> fail(String message) {
        return new BaseResult<>(500, message);
    }

    public static <T> BaseResult<T> fail(ResultCode code, String message) {
        return fail(code.getCode(), message);
    }

    public static <T> BaseResult<T> fail(Integer code, String message) {
        return new BaseResult<>(code, message);
    }
}