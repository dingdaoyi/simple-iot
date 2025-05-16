package com.github.dingdaoyi.controller.system;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.github.dingdaoyi.entity.User;
import com.github.dingdaoyi.model.base.R;
import com.github.dingdaoyi.model.enu.SystemCode;
import com.github.dingdaoyi.model.query.LoginQuery;
import com.github.dingdaoyi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

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
    public R<SaTokenInfo> doLogin(@RequestBody @Valid LoginQuery loginQuery) {

        Optional<User> optional = userService.getByUsername(loginQuery.getUsername());
        if (optional.isPresent()) {
            User user = optional.get();
            if (user.isLocked()) {
                return R.fail(SystemCode.BAD_REQUEST, "账号已被锁定");
            }
            if (BCrypt.checkpw(loginQuery.getPassword(), user.getPassword())) {
                StpUtil.login(user.getId());
                return R.success(StpUtil.getTokenInfo());
            }
            return R.fail(SystemCode.BAD_REQUEST, "登录密码错误");
        }
        return R.fail(SystemCode.BAD_REQUEST, "账号不存在");
    }

}
