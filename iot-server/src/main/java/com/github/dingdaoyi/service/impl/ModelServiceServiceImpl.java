package com.github.dingdaoyi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.dingdaoyi.entity.ServiceProperty;
import com.github.dingdaoyi.entity.enu.StatusEnum;
import com.github.dingdaoyi.model.query.StandardServiceAddQuery;
import com.github.dingdaoyi.model.vo.ModelServiceVO;
import com.github.dingdaoyi.service.CacheService;
import com.github.dingdaoyi.service.ServicePropertyService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.core.utils.$;
import net.dreamlu.mica.core.utils.JsonUtil;
import org.springframework.stereotype.Service;

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
    public List<ModelServiceVO> listByProductType(Integer productTypeId, Integer serviceType, Integer funcStatus) {
        List<ModelService> modelServices = baseMapper.selectList(
                Wrappers.<ModelService>lambdaQuery()
                        .eq(ModelService::getProductTypeId, productTypeId)
                        .eq($.isNotNull(serviceType), ModelService::getServiceType, serviceType)
                        .eq($.isNotNull(funcStatus), ModelService::getStatus, funcStatus)
        );
        if ($.isEmpty(modelServices)) {
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
    public Boolean save(StandardServiceAddQuery modelService) {
        ModelService serviceEntity = modelService.toEntity();
        int insert = baseMapper.insert(serviceEntity);
        if (insert < 0) {
            log.error("insert error,result zero:{}", JsonUtil.toJson(modelService));
            return false;
        }
        List<ServiceProperty> propertyList = modelService.getServiceProperties(serviceEntity.getId());
        if ($.isNotEmpty(propertyList)) {
            return servicePropertyService.saveBatch(propertyList);
        }
        //标准物模型都删了缓存
        cacheService.clearCache(CacheService.TSL_MODEL_CACHE);
        return true;
    }

    @Override
    public Boolean updateServiceStatus(Integer serviceId, Integer status) {
        boolean result = update(Wrappers.<ModelService>lambdaUpdate()
                .eq(ModelService::getId, serviceId)
                .set(ModelService::getStatus, status));
        if (result) {
            cacheService.clearCache(CacheService.TSL_MODEL_CACHE);
        }
        return result;
    }

    @Override
    public List<ModelServiceVO> listAllByProduct(Integer productId, Integer productTypeId) {
        LambdaQueryWrapper<ModelService> wrapper = Wrappers
                .<ModelService>lambdaQuery()
                .eq(ModelService::getProductTypeId, productTypeId)
                .eq(ModelService::getStatus, StatusEnum.ENABLED)
                .and(w -> w
                        .eq(ModelService::getCustom, false)
                        .or()
                        .eq(ModelService::getCustom, true)
                        .eq(ModelService::getProductId, productId)
                );
        return toListVo(list(wrapper));
    }
}
