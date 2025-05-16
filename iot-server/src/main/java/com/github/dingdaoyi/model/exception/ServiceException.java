package com.github.dingdaoyi.model.exception;

import com.github.dingdaoyi.model.base.R;
import com.github.dingdaoyi.model.enu.SystemCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 业务逻辑异常 Exception
 * @author dingyunwei
 */
@Data
@EqualsAndHashCode(callSuper = true)
public final class ServiceException extends RuntimeException {

    /**
     * 业务错误码
     *
     */
    @Getter
    private Integer code;
    /**
     * 错误提示
     */
    private String message;

    /**
     * 空构造方法，避免反序列化问题
     */
    public ServiceException() {
    }

    public ServiceException(SystemCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMsg();
    }

    public ServiceException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ServiceException(SystemCode systemCode, String message) {
        this.code = systemCode.getCode();
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public ServiceException setMessage(String message) {
        this.message = message;
        return this;
    }

    public R<Object> getResult() {
        return R.error(code, message);
    }
}
