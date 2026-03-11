package com.github.dingdaoyi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.dingdaoyi.core.base.PageResult;
import com.github.dingdaoyi.entity.Alarm;
import com.github.dingdaoyi.entity.enu.AlarmStatus;
import com.github.dingdaoyi.model.query.AlarmPageQuery;
import com.github.dingdaoyi.model.vo.AlarmPageVo;

import java.util.Optional;

/**
 * 告警服务接口
 * @author dingyunwei
 */
public interface AlarmService extends IService<Alarm> {

    /**
     * 分页查询
     */
    PageResult<AlarmPageVo> pageByQuery(AlarmPageQuery query);

    /**
     * 创建告警
     */
    Alarm createAlarm(Alarm alarm);

    /**
     * 清除告警
     */
    boolean clearAlarm(Integer alarmId, String clearBy);

    /**
     * 查找活动的告警
     */
    Optional<Alarm> findActiveAlarm(String deviceKey, String alarmType);

    /**
     * 统计活动告警数量
     */
    long countActiveAlarms();

    /**
     * 清除设备指定类型的告警
     */
    int clearByDeviceKeyAndType(String deviceKey, String alarmType);

    /**
     * 清除设备的所有活动告警
     */
    int clearAllByDeviceKey(String deviceKey);
}
