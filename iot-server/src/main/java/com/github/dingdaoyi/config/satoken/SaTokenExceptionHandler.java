package com.github.dingdaoyi.config.satoken;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.SaTokenException;

import com.github.dingdaoyi.model.enu.SysCodeEnum;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.core.result.R;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 * @author dyw
 */
@RestControllerAdvice
@Slf4j
@Order(Integer.MIN_VALUE)
public class SaTokenExceptionHandler {

    /**
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = NotLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public R<Object> notLoginException(NotLoginException e) {
        return R.fail(SysCodeEnum.UN_AUTHORIZED,e.getMessage());
    }

    /**
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Object> missingServletRequestParameterException(MissingServletRequestParameterException e) {
        return R.fail(SysCodeEnum.BAD_REQUEST,e.getParameterName()+"不能为空");
    }

    /**
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = NotPermissionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public R<Object> noPermissionException(NotPermissionException e) {
        return R.fail(SysCodeEnum.UNAUTHORIZED,"用户无操作权限:"+e.getPermission());
    }

    /**
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = NotRoleException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public R<Object> notRoleException(NotRoleException e) {
        return R.fail(SysCodeEnum.UNAUTHORIZED,"用户无"+e.getRole()+"角色！");
    }

    /**
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = SaTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public R<Object> saTokenException(SaTokenException e) {
        return R.fail(SysCodeEnum.UN_AUTHORIZED,"登陆失效,请重新登陆");
    }
}
