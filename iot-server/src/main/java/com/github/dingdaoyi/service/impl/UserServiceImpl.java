package com.github.dingdaoyi.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dingdaoyi.mapper.UserMapper;
import com.github.dingdaoyi.entity.User;
import com.github.dingdaoyi.service.UserService;
/**
 * @author dingyunwei
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    @Override
    public Optional<User> getByUsername(String username) {
        return baseMapper.findOneByUsername(username);
    }
}
