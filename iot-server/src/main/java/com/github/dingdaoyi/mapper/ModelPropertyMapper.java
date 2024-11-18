package com.github.dingdaoyi.mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.dingdaoyi.entity.ModelProperty;

/**
 * @author dingyunwei
 */
public interface ModelPropertyMapper extends BaseMapper<ModelProperty> {
    /**
     * 批量保存
     * @param list
     * @return
     */
    int insertList(@Param("list")List<ModelProperty> list);


}