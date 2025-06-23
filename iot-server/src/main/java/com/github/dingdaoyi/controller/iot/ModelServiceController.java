package com.github.dingdaoyi.controller.iot;

import cn.hutool.core.collection.CollectionUtil;
import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.core.enums.ResultCode;
import com.github.dingdaoyi.core.exception.BusinessException;
import com.github.dingdaoyi.model.query.ServiceAddQuery;
import com.github.dingdaoyi.model.query.ServiceUpdateQuery;
import com.github.dingdaoyi.model.vo.ModelServiceVO;
import com.github.dingdaoyi.service.ModelPropertyService;
import com.github.dingdaoyi.service.ModelServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author dingyunwei
 */
@RestController
@AllArgsConstructor
@RequestMapping("/model/service")
@Tag(name = "物模型-服务")
public class ModelServiceController {

    private final ModelServiceService modelServiceService;

    private final ModelPropertyService modelPropertyService;

    @GetMapping("standard")
    @Operation(summary = "根据产品id获取物模型数据")
    public BaseResult<List<ModelServiceVO>> list(@RequestParam(required = false) Integer serviceType,
                                        @RequestParam(required = false) String search,
                                        @RequestParam Integer productTypeId) {
        return BaseResult.success(modelServiceService.listByProductType(productTypeId,serviceType,search));
    }


    @GetMapping("custom")
    @Operation(summary = "获取自定义服务")
    public BaseResult<List<ModelServiceVO>> customList(@RequestParam(required = false) Integer serviceType,
                                        @RequestParam(required = false) String search,
                                        @RequestParam Integer productId) {
        return BaseResult.success(modelServiceService.listByProduct(productId,serviceType,search));
    }


    @PostMapping
    @Operation(summary = "新增服务")
    public BaseResult<Boolean> save(@RequestBody ServiceAddQuery modelService) {
        validateProperties(modelService);
        return BaseResult.success(modelServiceService.save(modelService));
    }

    @PutMapping
    @Operation(summary = "修改服务")
    public BaseResult<Boolean> update(@RequestBody ServiceUpdateQuery modelService) {
        validateProperties(modelService);
        return BaseResult.success(modelServiceService.update(modelService));
    }

    private void validateProperties(ServiceAddQuery modelService) {
        if (CollectionUtil.isNotEmpty(modelService.getInputParamIds())) {
            if (!modelPropertyService.allExists(modelService.getInputParamIds())) {
                throw new BusinessException(ResultCode.BAD_REQUEST,"请确保入参id");
            }
        }
    }

    @DeleteMapping("{id}")
    @Operation(summary = "根据产品id删除物模型数据")
    public BaseResult<Boolean> delete(@PathVariable  Integer id) {
        return BaseResult.success(modelServiceService.removeById(id));
    }
}
