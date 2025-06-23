package com.github.dingdaoyi.controller.iot;

import com.github.dingdaoyi.entity.Product;
import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.core.base.PageResult;
import com.github.dingdaoyi.core.enums.ResultCode;
import com.github.dingdaoyi.core.exception.BusinessException;
import com.github.dingdaoyi.model.query.ProductPageQuery;
import com.github.dingdaoyi.model.vo.ProductPageVo;
import com.github.dingdaoyi.model.vo.ProductVo;
import com.github.dingdaoyi.proto.model.tsl.TslModel;
import com.github.dingdaoyi.model.query.ProductAddQuery;
import com.github.dingdaoyi.model.query.ProductUpdateQuery;
import com.github.dingdaoyi.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author dingyunwei
 */
@AllArgsConstructor
@RestController
@RequestMapping("/product")
@Tag(name = "产品管理")
public class ProductController {
    private final ProductService productService;
    private final ProductTypeService productTypeService;
    private final TslModelService tslModelService;
    private final ModelServiceService modelServiceService;
    private final ModelPropertyService modelPropertyService;

    @GetMapping
    @Operation(summary = "根据产品类型和厂家获取")
    public BaseResult<List<Product>> list(@RequestParam Integer productTypeId,
                                 @RequestParam(required = false) String manufacturer) {
        return BaseResult.success(productService.listByType(productTypeId, manufacturer));
    }

    @PostMapping("page")
    @Operation(summary = "分页获取")
    public PageResult<ProductPageVo> page(@RequestBody ProductPageQuery pageQuery) {
        return productService.pageByQuery(pageQuery);
    }

    @PostMapping
    @Operation(summary = "添加产品")
    public BaseResult<Boolean> save(@RequestBody @Valid ProductAddQuery query) {
        if (!productTypeService.existsById(query.getProductTypeId())) {
            return BaseResult.fail(ResultCode.BAD_REQUEST.getCode(), "产品类型不存在");
        }
        if (productService.existsUnique(query.getModel(), query.getManufacturer(), query.getProductTypeId())) {
            return BaseResult.fail(ResultCode.BAD_REQUEST.getCode(), "产品已存在,请勿重复添加!");
        }
        return BaseResult.success(productService.add(query.toEntity()));
    }

    @PutMapping
    @Operation(summary = "修改产品")
    public BaseResult<Boolean> update(@RequestBody ProductUpdateQuery query) {
        return BaseResult.success(productService.updateById(query.toEntity()));
    }


    @DeleteMapping("{id}")
    @Operation(summary = "删除产品")
    public BaseResult<Boolean> delete(@PathVariable Integer id) {
        if (modelServiceService.existsByProduct((Integer) id)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "产品下存在物模型信息, 无法删除");
        }
        if (modelPropertyService.existsByProduct((Integer) id)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "产品下存在物模型信息, 无法删除");
        }
        return BaseResult.success(productService.removeById(id));
    }


    @GetMapping("{id}")
    @Operation(summary = "产品详情")
    public BaseResult<ProductVo> details(@PathVariable Integer id) {
        return productService.details(id).map(BaseResult::success)
                .orElse(BaseResult.fail("产品不存在"));
    }

    @GetMapping("tsl/{productId}")
    @Operation(summary = "物模型")
    public BaseResult<TslModel> tslDetails(@PathVariable Integer productId) {
        Product product = productService.getById(productId);
        if (product == null) {
            return BaseResult.fail(ResultCode.BAD_REQUEST.getCode(), "数据不存在");
        }
        return tslModelService.findByProductKey(product.getProductKey())
                .map(BaseResult::success).orElse(BaseResult.fail("未查询到物模型信息"));
    }


}
