package com.github.dingdaoyi.service;

import com.github.dingdaoyi.entity.Device;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.dingdaoyi.model.DTO.DeviceDTO;
import com.github.dingdaoyi.core.base.PageResult;
import com.github.dingdaoyi.model.query.DevicePageQuery;
import com.github.dingdaoyi.model.vo.DevicePageVo;
import com.github.dingdaoyi.model.vo.DeviceVo;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

public interface DeviceService extends IService<Device>{


    /**
     * 设备详情
     * @param id
     * @return
     */
    Optional<DeviceVo> details(Integer id);

    Optional<DeviceDTO> getByDeviceKey(String deviceKey);

    PageResult<DevicePageVo> pageByQuery(@Valid DevicePageQuery query);

    void updateOlinStatus(String deviceKey, boolean online);

    /**
     * 获取设备key
     * @param id id
     * @return
     */
    String getDeviceKey(Integer id);

    /**
     * 条件查询,所有数据可能为空,最多返回100条
     * @param productTypeId
     * @param productId
     * @param deviceKey
     * @return
     */
    List<Device> list(Integer productTypeId, Integer productId, String deviceKey);
}
