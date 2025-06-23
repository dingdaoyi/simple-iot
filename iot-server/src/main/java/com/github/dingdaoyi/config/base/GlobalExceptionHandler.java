package com.github.dingdaoyi.config.base;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.core.enums.ResultCode;
import com.github.dingdaoyi.core.exception.BusinessException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.postgresql.util.PSQLException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局异常处理
 * @author dyw
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {




    @ResponseBody
    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public BaseResult<Object> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        final MethodParameter parameter = e.getParameter();
        return BaseResult.fail(ResultCode.BAD_REQUEST.getCode(), "参数不存在:" + parameter.getParameterName());
    }

    @ResponseBody
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public BaseResult<Object> monitorExceptionHandler(HttpMessageNotReadableException e) {
        log.error("请求参数不规范:{}",e.getMessage());
        return BaseResult.fail(e.getMostSpecificCause().getMessage());
    }



    @ResponseBody
    @ExceptionHandler(value = BusinessException.class)
    public BaseResult<Object> businessException(BusinessException e) {
        return BaseResult.fail(e.getCode(), e.getMessage());
    }

    /**
     * 异常捕捉
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = FileSizeLimitExceededException.class)
    public BaseResult<Object> fileSizeLimitExceededException(FileSizeLimitExceededException e) {
        return BaseResult.fail("文件超过大小限制");
    }

    /**
     * 异常捕捉
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = {ConstraintViolationException.class, TransactionSystemException.class})
    public BaseResult<Object> constraintViolationException(ConstraintViolationException e) {
        final String message = e.getConstraintViolations()
                .stream().map(constraintViolation -> constraintViolation.getPropertyPath() + StringPool.COLON + constraintViolation.getMessage()).collect(Collectors.joining());
        log.error("参数校验异常:{}", message);
        return BaseResult.fail(ResultCode.PARAM_VALID_ERROR.getCode(), message);
    }

    /**
     * HttpRequestMethodNotSupportedException
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public BaseResult<Object> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return BaseResult.fail(ResultCode.METHOD_NOT_ALLOWED.getCode(), ResultCode.METHOD_NOT_ALLOWED.getMessage());
    }

    /**
     * 校验异常
     *
     * @param exception
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = BindException.class)
    public BaseResult<Object> bindException(BindException exception) {
        final String message = exception.getAllErrors()
                .stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(","));
        log.error("参数校验异常:{}", message);
        return BaseResult.fail(ResultCode.PARAM_VALID_ERROR.getCode(), message);
    }


    @ResponseBody
    @ExceptionHandler(value = NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResult<Object> exceptionHandler(NullPointerException e) {
        log.error("sytem_error", e);
        return BaseResult.fail(ResultCode.INTERNAL_SERVER_ERROR.getCode(), e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = PSQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResult<Object> exceptionHandler(PSQLException e) {
        log.error("sytem_error", e);
        return BaseResult.fail(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "服务器异常,请联系管理员");
    }

    @ResponseBody
    @ExceptionHandler(value = DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResult<Object> exceptionHandler(DataAccessException e) {
        log.error("sytem_error", e);
        return BaseResult.fail(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "数据异常");
    }

    @ResponseBody
    @ExceptionHandler(value = IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResult<Object> exceptionHandler(IllegalArgumentException e) {
        log.error("sytem_error", e);
        return BaseResult.fail(ResultCode.BAD_REQUEST.getCode(), e.getMessage());
    }
    /**
     * 异常捕捉
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResult<Object> exceptionHandler(Exception e) {
        log.error("sytem_error", e);
        return BaseResult.fail(ResultCode.INTERNAL_SERVER_ERROR.getCode(), ResultCode.INTERNAL_SERVER_ERROR.getMessage());
    }

    /**
     * 未找到请求
     * @param exception
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = NoResourceFoundException.class)
    public BaseResult<Object> noResourceFoundException(NoResourceFoundException exception) {
        log.error("路径异常:{}", exception.getMessage());
        return BaseResult.fail(ResultCode.NOT_FOUND.getCode(), ResultCode.NOT_FOUND.getMessage());
    }

    /**
     * 参数校验异常处理
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = HandlerMethodValidationException.class)
    public BaseResult<Object> handlerMethodValidationException(HandlerMethodValidationException e) {
        List<String> collect = e.getValueResults().stream().map(result -> {
            List<String> list = result.getResolvableErrors().stream().map(MessageSourceResolvable::getDefaultMessage).toList();
            return list.getFirst();
        }).toList();
        return BaseResult.fail(ResultCode.PARAM_VALID_ERROR.getCode(), String.join(",",collect));
    }
}
