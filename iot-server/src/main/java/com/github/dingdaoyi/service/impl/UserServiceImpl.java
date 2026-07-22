package com.github.dingdaoyi.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import com.github.dingdaoyi.core.enums.ResultCode;
import com.github.dingdaoyi.core.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dingdaoyi.mapper.UserMapper;
import com.github.dingdaoyi.entity.User;
import com.github.dingdaoyi.service.UserService;

/**
 * @author dingyunwei
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public Optional<User> getByUsername(String username) {
        return baseMapper.findOneByUsername(username);
    }

    @Override
    public boolean changePassword(Integer userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "用户不存在");
        }
        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "原密码错误");
        }
        validatePassword(newPassword);
        user.setPassword(BCrypt.hashpw(newPassword));
        user.setForceChangePwd(false);
        return updateById(user);
    }

    private static final Pattern PWD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");

    /**
     * 密码复杂度校验: 8位+大小写+数字
     */
    public static void validatePassword(String password) {
        if (password == null || !PWD_PATTERN.matcher(password).matches()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "密码需至少8位，包含大小写字母和数字");
        }
    }
}
