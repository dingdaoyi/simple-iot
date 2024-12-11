package com.github.dingdaoyi.service.impl;

import com.github.dingdaoyi.entity.ModelProperty;
import com.github.dingdaoyi.entity.Product;
import com.github.dingdaoyi.entity.enu.ParamType;
import com.github.dingdaoyi.entity.enu.ServiceTypeEnum;
import com.github.dingdaoyi.proto.model.tsl.TslEvent;
import com.github.dingdaoyi.proto.model.tsl.TslModel;
import com.github.dingdaoyi.proto.model.tsl.TslProperty;
import com.github.dingdaoyi.proto.model.tsl.TslService;
import com.github.dingdaoyi.model.vo.ModelServiceVO;
import com.github.dingdaoyi.service.ModelPropertyService;
import com.github.dingdaoyi.service.ModelServiceService;
import com.github.dingdaoyi.service.ProductService;
import com.github.dingdaoyi.service.TslModelService;
import lombok.AllArgsConstructor;
import net.dreamlu.mica.core.utils.$;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * @author dingyunwei
 */
@Service
@AllArgsConstructor
public class TslModelServiceImpl implements TslModelService {
    private final ProductService productService;
    private final ModelServiceService modelServiceService;
    private final ModelPropertyService modelPropertyService;

    @Cacheable(value = "tslModelCache", key = "#productKey")
    @Override
    public Optional<TslModel> findByProductKey(String productKey) {
        return productService.getByProductKey(productKey)
                .map(product -> {
                    TslModel tslModelDTO = new TslModel();
                    tslModelDTO.setProductKey(product.getProductKey());
                    processProductData(product, tslModelDTO);
                    return tslModelDTO;
                });
    }

    private void processProductData(Product product, TslModel tslModelDTO) {
        List<ModelServiceVO> serviceList = modelServiceService.listAllByProduct(product.getId(), product.getProductTypeId());
        Map<ServiceTypeEnum, List<ModelServiceVO>> servicesMap = serviceList.stream()
                .collect(Collectors.groupingBy(ModelServiceVO::getServiceType));

        Map<Integer, TslProperty> propertiesMap = new HashMap<>();
        Map<Integer, TslProperty> paramsMap = new HashMap<>();

        modelPropertyService.listByProduct(product.getId(), product.getProductTypeId())
                .forEach(property -> {
                    if (property.getParamType() == ParamType.PROPERTY) {
                        addPropToMap(propertiesMap, property);
                    } else {
                        addPropToMap(paramsMap, property);
                    }
                });

        Map<Integer, TslProperty> allProperties = new HashMap<>(propertiesMap);
        allProperties.putAll(paramsMap);

        tslModelDTO.setProperties(new ArrayList<>(propertiesMap.values()));
        tslModelDTO.setServices(mapServicesToDTOs(servicesMap, ServiceTypeEnum.SERVICE, allProperties,
                (svc, params) -> {
                    TslService tslServiceDTO = new TslService(getTslPropertyDTOS(allProperties, svc.getInputParamIds()), params);
                    $.copy(svc, tslServiceDTO);
                    return tslServiceDTO;
                }));
        tslModelDTO.setEvents(mapServicesToDTOs(servicesMap, ServiceTypeEnum.EVENT, allProperties, (modelServiceVO, tslPropertyDTOS) -> {
            TslEvent tslEventDTO = new TslEvent(tslPropertyDTOS);
            $.copy(modelServiceVO, tslEventDTO);
            return tslEventDTO;
        }));
    }

    private static List<TslProperty> getTslPropertyDTOS(Map<Integer, TslProperty> paramsMap, List<Integer> outputParamIds) {
        return paramsMap.entrySet()
                .stream()
                .filter(entry -> outputParamIds.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .toList();
    }

    private <T> List<T> mapServicesToDTOs(Map<ServiceTypeEnum, List<ModelServiceVO>> servicesMap,
                                          ServiceTypeEnum type,
                                          Map<Integer, TslProperty> allProperties,
                                          BiFunction<ModelServiceVO, List<TslProperty>, T> mapper) {
        return servicesMap.getOrDefault(type, List.of())
                .stream()
                .map(svc -> {
                    List<TslProperty> params = getTslPropertyDTOS(allProperties, svc.getOutputParamIds());
                    return mapper.apply(svc, params);
                })
                .toList();
    }

    private void addPropToMap(Map<Integer, TslProperty> paramsMap, ModelProperty property) {
        if (paramsMap.containsKey(property.getParentId())) {
            TslProperty tslProperty = paramsMap.get(property.getParentId());
            List<TslProperty> children = Optional.ofNullable(tslProperty.getChildren())
                    .orElseGet(ArrayList::new);
            children.add(property.toTsl());
            tslProperty.setChildren(children);
        } else {
            paramsMap.put(property.getId(), property.toTsl());
        }
    }
}
