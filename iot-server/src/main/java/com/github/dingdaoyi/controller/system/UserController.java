package com.github.dingdaoyi.controller.system;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.github.dingdaoyi.entity.User;
import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.core.enums.ResultCode;
import com.github.dingdaoyi.model.query.ChangePasswordQuery;
import com.github.dingdaoyi.model.query.LoginQuery;
import com.github.dingdaoyi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author dingyunwei
 */
@RequestMapping("user")
@RestController
@Tag(name = "用户管理")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("login")
    @Operation(summary  = "登录")
    public BaseResult<Map<String, Object>> doLogin(@RequestBody @Valid LoginQuery loginQuery) {
        Optional<User> optional = userService.getByUsername(loginQuery.getUsername());
        if (optional.isPresent()) {
            User user = optional.get();
            if (user.isLocked()) {
                return BaseResult.fail(ResultCode.BAD_REQUEST.getCode(), "账号已被锁定");
            }
            if (BCrypt.checkpw(loginQuery.getPassword(), user.getPassword())) {
                StpUtil.login(user.getId());
                StpUtil.getSession().set("user", user);
                SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
                Map<String, Object> data = new HashMap<>();
                data.put("tokenInfo", tokenInfo);
                data.put("forceChangePwd", Boolean.TRUE.equals(user.getForceChangePwd()));
                return BaseResult.success(data);
            }
            return BaseResult.fail(ResultCode.BAD_REQUEST.getCode(), "登录密码错误");
        }
        return BaseResult.fail(ResultCode.BAD_REQUEST.getCode(), "账号不存在");
    }

    @PostMapping("changePassword")
    @Operation(summary = "修改密码")
    public BaseResult<Void> changePassword(@RequestBody @Valid ChangePasswordQuery query) {
        Integer userId = Integer.parseInt(StpUtil.getLoginIdAsString());
        userService.changePassword(userId, query.getOldPassword(), query.getNewPassword());
        StpUtil.logout();
        return BaseResult.success();
    }
}
