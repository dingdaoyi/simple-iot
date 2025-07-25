package com.github.dingdaoyi.controller.iot;

import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.core.enums.ResultCode;
import com.github.dingdaoyi.model.query.ModelPropertyUpdateQuery;
import com.github.dingdaoyi.model.query.ProductPropertyAddQuery;
import com.github.dingdaoyi.model.query.StandardPropertyAddQuery;
import com.github.dingdaoyi.model.vo.ModelPropertyVo;
import com.github.dingdaoyi.service.ModelPropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author dingyunwei
 */
@RestController
@AllArgsConstructor
@RequestMapping("/model/property")
@Tag(name = "物模型-属性")
public class ModelPropertyController {
    private final ModelPropertyService modelPropertyService;

    @GetMapping("product/type")
    @Operation(summary = "根据产品id获取物模型,属性和参数")
    public BaseResult<List<ModelPropertyVo>> list(@RequestParam(required = false) Integer paramType,
                                         @RequestParam Integer productTypeId,
                                         @RequestParam(required = false) Integer productId,
                                         @RequestParam(required = false) String search,
                                         @RequestParam(defaultValue = "false") Boolean all) {
        return BaseResult.success(modelPropertyService.listByProductType(productTypeId, productId, paramType,search,all)
                .stream()
                .map(ModelPropertyVo::new).toList());
    }

    @PostMapping("product/type")
    @Operation(summary = "保存标准物模型属性")
    public BaseResult<Boolean> saveStandardProperty(@Valid @RequestBody StandardPropertyAddQuery property) {
        if (modelPropertyService.exists(property.getIdentifier(), property.getProductTypeId())) {
            return BaseResult.fail(ResultCode.BAD_REQUEST, "标识符已占用!");
        }
        return BaseResult.success(modelPropertyService.saveStandardProperty(property));
    }

    @PostMapping("product")
    @Operation(summary = "保存自定义属性")
    public BaseResult<Boolean> saveProductProperty(@RequestBody ProductPropertyAddQuery property) {
        if (modelPropertyService.exists(property.getIdentifier(), property.getProductTypeId(), property.getProductId())) {
            return BaseResult.fail(ResultCode.BAD_REQUEST, "标识符已占用!");
        }
        return BaseResult.success(modelPropertyService.saveProductProperty(property));
    }

    @PutMapping("product")
    @Operation(summary = "修改物模型")
    public BaseResult<Boolean> update(@RequestBody ModelPropertyUpdateQuery query) {
        return BaseResult.success(modelPropertyService.update(query));
    }

    @DeleteMapping("{id}")
    @Operation(summary = "删除物属性")
    public BaseResult<Boolean> delete(@PathVariable Integer id) {
        return BaseResult.success(modelPropertyService.removeById(id));
    }

}
