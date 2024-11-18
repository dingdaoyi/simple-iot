package com.github.dingdaoyi.service;

import com.github.dingdaoyi.entity.ServiceProperty;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ServicePropertyService extends IService<ServiceProperty>{


    /**
     * 根据服务id查询
     * @param serviceId 服务id
     * @return 参数property的id
     */
    List<Integer> listByServiceId(Integer serviceId,int paramType);
}
