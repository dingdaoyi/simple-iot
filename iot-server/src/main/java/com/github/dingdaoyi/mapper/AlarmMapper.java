package com.github.dingdaoyi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.dingdaoyi.entity.Alarm;
import com.github.dingdaoyi.model.query.AlarmPageQuery;
import com.github.dingdaoyi.model.vo.AlarmPageVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

/**
 * 告警 Mapper
 * @author dingyunwei
 */
@Mapper
public interface AlarmMapper extends BaseMapper<Alarm> {

    /**
     * 分页查询
     */
    Page<AlarmPageVo> pageByQuery(Page<AlarmPageVo> page, @Param("query") AlarmPageQuery query);

    /**
     * 查找活动的告警
     */
    Optional<Alarm> findActiveAlarm(@Param("deviceKey") String deviceKey,
                                     @Param("alarmType") String alarmType);
}
