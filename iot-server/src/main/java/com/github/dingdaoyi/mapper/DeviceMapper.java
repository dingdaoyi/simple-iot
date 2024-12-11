package com.github.dingdaoyi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.dingdaoyi.entity.Device;
import com.github.dingdaoyi.model.DTO.DeviceDTO;
import com.github.dingdaoyi.model.query.DevicePageQuery;
import com.github.dingdaoyi.model.vo.DevicePageVo;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

/**
 * @author dingyunwei
 */
public interface DeviceMapper extends BaseMapper<Device> {

    Optional<DeviceDTO> findByDeviceKey(@Param("deviceKey") String deviceKey);

    Page<DevicePageVo> pageByQuery(Page<DevicePageVo> page, @Param("query") DevicePageQuery query);
}