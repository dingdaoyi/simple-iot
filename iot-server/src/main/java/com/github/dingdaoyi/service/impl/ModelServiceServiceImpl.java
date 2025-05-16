package com.github.dingdaoyi.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.dingdaoyi.entity.ServiceProperty;
import com.github.dingdaoyi.model.query.ServiceAddQuery;
import com.github.dingdaoyi.model.query.ServiceUpdateQuery;
import com.github.dingdaoyi.model.vo.ModelServiceVO;
import com.github.dingdaoyi.service.CacheService;
import com.github.dingdaoyi.service.ServicePropertyService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dingdaoyi.entity.ModelService;
import com.github.dingdaoyi.mapper.ModelServiceMapper;
import com.github.dingdaoyi.service.ModelServiceService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author dingyunwei
 */
@Service
@Slf4j
public class ModelServiceServiceImpl extends ServiceImpl<ModelServiceMapper, ModelService> implements ModelServiceService {

    @Resource
    private ServicePropertyService servicePropertyService;

    @Resource
    private CacheService cacheService;

    @Override
    public List<ModelServiceVO> listByProductType(Integer productTypeId, Integer serviceType, String search) {
        List<ModelService> modelServices = baseMapper.selectList(
                Wrappers.<ModelService>lambdaQuery()
                        .eq(ModelService::getProductTypeId, productTypeId)
                        .eq(ObjectUtils.isNotNull(serviceType), ModelService::getServiceType, serviceType)
                        .and(StringUtils.isNotBlank(search), w ->
                                w.like(ModelService::getName, search)
                                        .or()
                                        .eq(ModelService::getIdentifier, search)
                        )
        );
        if (CollectionUtil.isEmpty(modelServices)) {
            return List.of();
        }
        // 设置出参, 入参
        return toListVo(modelServices);
    }

    @Override
    public List<ModelServiceVO> listByProduct(Integer productId, Integer serviceType, String search) {
        List<ModelService> modelServices = baseMapper.selectList(
                Wrappers.<ModelService>lambdaQuery()
                        .eq(ModelService::getProductId, productId)
                        .eq(ModelService::getCustom,true)
                        .eq(ObjectUtils.isNotNull(serviceType), ModelService::getServiceType, serviceType)
                        .and(StringUtils.isNotBlank(search), w ->
                                w.like(ModelService::getName, search)
                                        .or()
                                        .eq(ModelService::getIdentifier, search)
                        )
        );
        if (CollectionUtil.isEmpty(modelServices)) {
            return List.of();
        }
        // 设置出参, 入参
        return toListVo(modelServices);
    }
    private List<ModelServiceVO> toListVo(List<ModelService> modelServices) {
        return modelServices.stream().map(modelService -> {
            ModelServiceVO serviceVO = ModelServiceVO.build(modelService);
            serviceVO.setInputParamIds(servicePropertyService.listByServiceId(modelService.getId(), ServiceProperty.INPUT_TYPE));
            serviceVO.setOutputParamIds(servicePropertyService.listByServiceId(modelService.getId(), ServiceProperty.OUTPUT_TYPE));
            return serviceVO;
        }).toList();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean save(ServiceAddQuery modelService) {
        ModelService serviceEntity = modelService.toEntity();
        int insert = baseMapper.insert(serviceEntity);
        if (insert < 0) {
            log.error("insert error,result zero:{}", JSONUtil.toJsonStr(modelService));
            return false;
        }
        List<ServiceProperty> propertyList = modelService.getServiceProperties(serviceEntity.getId());
        if (CollectionUtil.isNotEmpty(propertyList)) {
            return servicePropertyService.saveBatch(propertyList);
        }
        //标准物模型都删了缓存
        cacheService.clearCache(CacheService.TSL_MODEL_CACHE);
        return true;
    }

    @Override
    public List<ModelServiceVO> listAllByProduct(Integer productId, Integer productTypeId) {
        LambdaQueryWrapper<ModelService> wrapper = Wrappers
                .<ModelService>lambdaQuery()
                .eq(ModelService::getProductTypeId, productTypeId)
                .and(w -> w
                        .eq(ModelService::getCustom, false)
                        .or()
                        .eq(ModelService::getCustom, true)
                        .eq(ModelService::getProductId, productId)
                );
        return toListVo(list(wrapper));
    }

    @Override
    public Boolean update(ServiceUpdateQuery modelService) {
        ModelService serviceEntity = modelService.toEntity();
        int insert = baseMapper.updateById(serviceEntity);
        if (insert < 0) {
            log.error("修改失败:{}", JSONUtil.toJsonStr(modelService));
            return false;
        }
        // 清理之前的绑定
        servicePropertyService.removeByServiceId(serviceEntity.getId());
        List<ServiceProperty> propertyList = modelService.getServiceProperties(serviceEntity.getId());
        if (CollectionUtil.isNotEmpty(propertyList)) {
            return servicePropertyService.saveBatch(propertyList);
        }
        //标准物模型都删了缓存
        cacheService.clearCache(CacheService.TSL_MODEL_CACHE);
        return true;
    }

    @Override
    public boolean removeById(Serializable id) {
        try {
            return super.removeById(id);
        } finally {
            //标准物模型都删了缓存
            cacheService.clearCache(CacheService.TSL_MODEL_CACHE);
        }
    }

    @Override
    public boolean existsByProduct(Integer productId) {
        return exists(Wrappers
                .<ModelService>lambdaQuery()
                .eq(ModelService::getProductId, productId));
    }

}
