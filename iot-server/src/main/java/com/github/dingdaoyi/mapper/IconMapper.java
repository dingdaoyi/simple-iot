package com.github.dingdaoyi.mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.dingdaoyi.entity.Icon;

/**
 * @author dingyunwei
 */
public interface IconMapper extends BaseMapper<Icon> {

    String findPathById(@Param("id")Integer id);

}