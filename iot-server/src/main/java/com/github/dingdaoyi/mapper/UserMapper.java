package com.github.dingdaoyi.mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.dingdaoyi.entity.User;

import java.util.Optional;

/**
 * @author dingyunwei
 */
public interface UserMapper extends BaseMapper<User> {

    Optional<User> findOneByUsername(@Param("username")String username);


}