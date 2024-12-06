package com.github.dingdaoyi.service;

import com.github.dingdaoyi.entity.ModelService;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.dingdaoyi.model.query.ServiceAddQuery;
import com.github.dingdaoyi.model.query.ServiceUpdateQuery;
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
     * @param search 搜索条件
     * @return
     */
    List<ModelServiceVO> listByProductType(Integer productTypeId, Integer serviceType, String search);

    Boolean save(ServiceAddQuery modelService);

    /**
     * 获取所有服务,事件
     * @param productId
     * @param productTypeId
     * @return
     */
    List<ModelServiceVO> listAllByProduct(Integer productId, Integer productTypeId);


    Boolean update(ServiceUpdateQuery modelService);

    boolean existsByProduct(Integer productId);

    /**
     *
     * @param productId 产品id
     * @param serviceType 服务类型
     * @param search 搜索字段
     * @return
     */
    List<ModelServiceVO> listByProduct(Integer productId, Integer serviceType, String search);
}
