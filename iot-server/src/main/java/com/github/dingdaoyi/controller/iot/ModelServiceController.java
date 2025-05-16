package com.github.dingdaoyi.controller.iot;

import cn.hutool.core.collection.CollectionUtil;
import com.github.dingdaoyi.model.base.R;
import com.github.dingdaoyi.model.enu.SystemCode;
import com.github.dingdaoyi.model.exception.ServiceException;
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
    public R<List<ModelServiceVO>> list(@RequestParam(required = false) Integer serviceType,
                                        @RequestParam(required = false) String search,
                                        @RequestParam Integer productTypeId) {
        return R.success(modelServiceService.listByProductType(productTypeId,serviceType,search));
    }


    @GetMapping("custom")
    @Operation(summary = "获取自定义服务")
    public R<List<ModelServiceVO>> customList(@RequestParam(required = false) Integer serviceType,
                                        @RequestParam(required = false) String search,
                                        @RequestParam Integer productId) {
        return R.success(modelServiceService.listByProduct(productId,serviceType,search));
    }


    @PostMapping
    @Operation(summary = "新增服务")
    public R<Boolean> save(@RequestBody ServiceAddQuery modelService) {
        validateProperties(modelService);
        return R.success(modelServiceService.save(modelService));
    }

    @PutMapping
    @Operation(summary = "修改服务")
    public R<Boolean> update(@RequestBody ServiceUpdateQuery modelService) {
        validateProperties(modelService);
        return R.success(modelServiceService.update(modelService));
    }

    private void validateProperties(ServiceAddQuery modelService) {
        if (CollectionUtil.isNotEmpty(modelService.getInputParamIds())) {
            if (!modelPropertyService.allExists(modelService.getInputParamIds())) {
                throw new ServiceException(SystemCode.BAD_REQUEST,"请确保入参id");
            }
        }
    }

    @DeleteMapping("{id}")
    @Operation(summary = "根据产品id删除物模型数据")
    public R<Boolean> delete(@PathVariable  Integer id) {
        return R.success(modelServiceService.removeById(id));
    }
}
