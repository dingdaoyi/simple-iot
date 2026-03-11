package com.github.dingdaoyi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.dingdaoyi.entity.PushConfig;
import com.github.dingdaoyi.model.query.PushConfigPageQuery;
import com.github.dingdaoyi.model.vo.PushConfigPageVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 推送配置 Mapper
 * @author dingyunwei
 */
@Mapper
public interface PushConfigMapper extends BaseMapper<PushConfig> {

    /**
     * 分页查询
     */
    Page<PushConfigPageVo> pageByQuery(Page<PushConfigPageVo> page, @Param("query") PushConfigPageQuery query);

    /**
     * 获取所有启用的配置
     */
    List<PushConfig> listEnabled();
}
