package com.github.dingdaoyi.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dingdaoyi.entity.ServiceProperty;
import com.github.dingdaoyi.mapper.ServicePropertyMapper;
import com.github.dingdaoyi.service.ServicePropertyService;
/**
 * @author dingyunwei
 */
@Service
public class ServicePropertyServiceImpl extends ServiceImpl<ServicePropertyMapper, ServiceProperty> implements ServicePropertyService{

    @Override
    public List<Integer> listByServiceId(Integer serviceId,int paramType) {
        return baseMapper.listPropertyIdByServiceIdAndParamType(serviceId, paramType);
    }
}
