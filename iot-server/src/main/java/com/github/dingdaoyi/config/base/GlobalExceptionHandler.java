package com.github.dingdaoyi.config.base;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.github.dingdaoyi.model.enu.SysCodeEnum;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.core.exception.ServiceException;
import net.dreamlu.mica.core.result.R;
import net.dreamlu.mica.core.result.SystemCode;
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
    public R<Object> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        final MethodParameter parameter = e.getParameter();
        return R.fail(SysCodeEnum.BAD_REQUEST, "参数不存在:" + parameter.getParameterName());
    }

    @ResponseBody
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public R<Object> monitorExceptionHandler(HttpMessageNotReadableException e) {
        log.error("请求参数不规范:{}",e.getMessage());
        return R.fail(e.getMostSpecificCause().getMessage());
    }



    /**
     * 异常捕捉
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = ServiceException.class)
    public R<Object> serviceException(ServiceException e) {
        return e.getResult();
    }

    /**
     * 异常捕捉
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = {ConstraintViolationException.class, TransactionSystemException.class})
    public R<Object> constraintViolationException(ConstraintViolationException e) {
        final String message = e.getConstraintViolations()
                .stream().map(constraintViolation -> constraintViolation.getPropertyPath() + StringPool.COLON + constraintViolation.getMessage()).collect(Collectors.joining());
        log.error("参数校验异常:{}", message);
        return R.fail(SystemCode.PARAM_VALID_ERROR, message);
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
    public R<Object> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return R.fail(SystemCode.METHOD_NOT_SUPPORTED);
    }

    /**
     * 校验异常
     *
     * @param exception
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = BindException.class)
    public R<Object> bindException(BindException exception) {
        final String message = exception.getAllErrors()
                .stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(","));
        log.error("参数校验异常:{}", message);
        return R.fail(SystemCode.PARAM_VALID_ERROR, message);
    }


    @ResponseBody
    @ExceptionHandler(value = NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Object> exceptionHandler(NullPointerException e) {
        log.error("sytem_error", e);
        return R.fail(SysCodeEnum.NPE_ERROR,e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = PSQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Object> exceptionHandler(PSQLException e) {
        log.error("sytem_error", e);
        return R.fail(SystemCode.FAILURE,"服务器异常,请联系管理员");
    }

    @ResponseBody
    @ExceptionHandler(value = DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Object> exceptionHandler(DataAccessException e) {
        log.error("sytem_error", e);
        return R.fail(SystemCode.FAILURE,"数据异常");
    }

    @ResponseBody
    @ExceptionHandler(value = IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Object> exceptionHandler(IllegalArgumentException e) {
        log.error("sytem_error", e);
        return R.fail(SysCodeEnum.BAD_REQUEST,e.getMessage());
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
    public R<Object> exceptionHandler(Exception e) {
        log.error("sytem_error", e);
        return R.fail(SystemCode.FAILURE);
    }

    /**
     * 未找到请求
     * @param exception
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = NoResourceFoundException.class)
    public R<Object> noResourceFoundException(NoResourceFoundException exception) {
        log.error("路径异常:{}", exception.getMessage());
        return R.fail(SystemCode.NOT_FOUND, SystemCode.NOT_FOUND.getMsg());
    }

    /**
     * 参数校验异常处理
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = HandlerMethodValidationException.class)
    public R<Object> monitorExceptionHandler(HandlerMethodValidationException e) {
        List<String> collect = e.getAllValidationResults().stream().map(result -> {
            List<String> list = result.getResolvableErrors().stream().map(MessageSourceResolvable::getDefaultMessage).toList();
            return list.getFirst();
        }).toList();
        return R.fail(SystemCode.PARAM_VALID_ERROR, String.join(",",collect));
    }
}
