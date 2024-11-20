package com.github.dingdaoyi.service.impl;

import com.github.dingdaoyi.entity.ModelProperty;
import com.github.dingdaoyi.entity.ModelService;
import com.github.dingdaoyi.entity.Product;
import com.github.dingdaoyi.entity.enu.ParamType;
import com.github.dingdaoyi.entity.enu.ServiceTypeEnum;
import com.github.dingdaoyi.model.DTO.TslEventDTO;
import com.github.dingdaoyi.model.DTO.TslModelDTO;
import com.github.dingdaoyi.model.DTO.TslPropertyDTO;
import com.github.dingdaoyi.model.DTO.TslServiceDTO;
import com.github.dingdaoyi.model.vo.ModelServiceVO;
import com.github.dingdaoyi.service.ModelPropertyService;
import com.github.dingdaoyi.service.ModelServiceService;
import com.github.dingdaoyi.service.ProductService;
import com.github.dingdaoyi.service.TslModelService;
import lombok.AllArgsConstructor;
import net.dreamlu.mica.core.utils.$;
import net.dreamlu.mica.core.utils.CollectionUtil;
import org.jetbrains.annotations.NotNull;
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
    public Optional<TslModelDTO> findByProductKey(String productKey) {
        return productService.getByProductKey(productKey)
                .map(product -> {
                    TslModelDTO tslModelDTO = new TslModelDTO();
                    tslModelDTO.setProductKey(product.getProductKey());
                    processProductData(product, tslModelDTO);
                    return tslModelDTO;
                });
    }

    private void processProductData(Product product, TslModelDTO tslModelDTO) {
        List<ModelServiceVO> serviceList = modelServiceService.listAllByProduct(product.getId(), product.getProductTypeId());
        Map<ServiceTypeEnum, List<ModelServiceVO>> servicesMap = serviceList.stream()
                .collect(Collectors.groupingBy(ModelServiceVO::getServiceType));

        Map<Integer, TslPropertyDTO> propertiesMap = new HashMap<>();
        Map<Integer, TslPropertyDTO> paramsMap = new HashMap<>();

        modelPropertyService.listByProduct(product.getId(), product.getProductTypeId())
                .forEach(property -> {
                    if (property.getParamType() == ParamType.PROPERTY) {
                        addPropToMap(propertiesMap, property);
                    } else {
                        addPropToMap(paramsMap, property);
                    }
                });

        Map<Integer, TslPropertyDTO> allProperties = new HashMap<>(propertiesMap);
        allProperties.putAll(paramsMap);

        tslModelDTO.setProperties(new ArrayList<>(propertiesMap.values()));
        tslModelDTO.setServices(mapServicesToDTOs(servicesMap, ServiceTypeEnum.SERVICE, allProperties,
                (svc, params) -> new TslServiceDTO(svc, getTslPropertyDTOS(allProperties, svc.getInputParamIds()), params)));
        tslModelDTO.setEvents(mapServicesToDTOs(servicesMap, ServiceTypeEnum.EVENT, allProperties, TslEventDTO::new));
    }

    private static List<TslPropertyDTO> getTslPropertyDTOS(Map<Integer, TslPropertyDTO> paramsMap, List<Integer> outputParamIds) {
        return paramsMap.entrySet()
                .stream()
                .filter(entry -> outputParamIds.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .toList();
    }

    private <T> List<T> mapServicesToDTOs(Map<ServiceTypeEnum, List<ModelServiceVO>> servicesMap,
                                          ServiceTypeEnum type,
                                          Map<Integer, TslPropertyDTO> allProperties,
                                          BiFunction<ModelServiceVO, List<TslPropertyDTO>, T> mapper) {
        return servicesMap.getOrDefault(type, List.of())
                .stream()
                .map(svc -> {
                    List<TslPropertyDTO> params = getTslPropertyDTOS(allProperties, svc.getOutputParamIds());
                    return mapper.apply(svc, params);
                })
                .toList();
    }

    private void addPropToMap(Map<Integer, TslPropertyDTO> paramsMap, ModelProperty property) {
        paramsMap.compute(property.getParentId(), (parentId, parentDTO) -> {
            if (parentDTO == null) {
                parentDTO = TslPropertyDTO.of(property);
            } else {
                List<TslPropertyDTO> children = Optional.ofNullable(parentDTO.getChildren())
                        .orElseGet(ArrayList::new);
                children.add(TslPropertyDTO.of(property));
                parentDTO.setChildren(children);
            }
            return parentDTO;
        });
    }
}
