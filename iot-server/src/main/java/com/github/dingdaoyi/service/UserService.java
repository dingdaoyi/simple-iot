package com.github.dingdaoyi.service;

import com.github.dingdaoyi.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Optional;

/**
 * @author dingyunwei
 */
public interface UserService extends IService<User>{


    Optional<User> getByUsername(String username);
}
