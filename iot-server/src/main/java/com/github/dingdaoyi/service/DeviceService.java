package com.github.dingdaoyi.service;

import com.github.dingdaoyi.entity.Device;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.dingdaoyi.model.vo.DeviceVo;

import java.util.Optional;

public interface DeviceService extends IService<Device>{


    /**
     * 设备详情
     * @param id
     * @return
     */
    Optional<DeviceVo> details(Integer id);
}
