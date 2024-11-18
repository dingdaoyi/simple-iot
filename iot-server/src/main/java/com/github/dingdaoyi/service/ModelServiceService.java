package com.github.dingdaoyi.service;

import com.github.dingdaoyi.entity.ModelService;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.dingdaoyi.model.query.StandardServiceAddQuery;
import com.github.dingdaoyi.model.vo.ModelServiceVO;

import java.util.List;

/**
 * @author dingyunwei
 */
public interface ModelServiceService extends IService<ModelService>{

    /**
     *  查询标准物模型事件
     * @param productTypeId 品类id
     * @param serviceType  服务类型 可选
     * @param funcStatus 状态,可选
     * @return
     */
    List<ModelServiceVO> listByProductType(Integer productTypeId, Integer serviceType, Integer funcStatus);

    Boolean save(StandardServiceAddQuery modelService);

    /**
     * 修改状态
     * @param serviceId 服务id
     * @param status 状态
     * @return
     */
    Boolean updateServiceStatus(Integer serviceId,Integer status);
}
