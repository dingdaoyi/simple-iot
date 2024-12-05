package com.github.dingdaoyi.controller.iot;

import com.github.dingdaoyi.entity.Product;
import com.github.dingdaoyi.model.PageResult;
import com.github.dingdaoyi.model.query.ProductPageQuery;
import com.github.dingdaoyi.model.vo.ProductPageVo;
import com.github.dingdaoyi.proto.model.tsl.TslModel;
import com.github.dingdaoyi.model.enu.SysCodeEnum;
import com.github.dingdaoyi.model.query.ProductAddQuery;
import com.github.dingdaoyi.model.query.ProductUpdateQuery;
import com.github.dingdaoyi.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import net.dreamlu.mica.core.exception.ServiceException;
import net.dreamlu.mica.core.result.R;
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
    public R<List<Product>> list(@RequestParam Integer productTypeId,
                                 @RequestParam(required = false) String manufacturer) {
        return R.success(productService.listByType(productTypeId, manufacturer));
    }

    @PostMapping("page")
    @Operation(summary = "分页获取")
    public PageResult<ProductPageVo> page(@RequestBody ProductPageQuery pageQuery) {
        return productService.pageByQuery(pageQuery);
    }

    @PostMapping
    @Operation(summary = "添加产品")
    public R<Boolean> save(@RequestBody @Valid ProductAddQuery query) {
        if (!productTypeService.existsById(query.getProductTypeId())) {
            return R.fail(SysCodeEnum.BAD_REQUEST, "产品类型不存在");
        }
        if (productService.existsUnique(query.getModel(), query.getManufacturer(), query.getProductTypeId())) {
            return R.fail(SysCodeEnum.BAD_REQUEST, "产品已存在,请勿重复添加!");
        }
        return R.success(productService.add(query.toEntity()));
    }

    @PutMapping
    @Operation(summary = "修改产品")
    public R<Boolean> update(@RequestBody ProductUpdateQuery query) {
        return R.success(productService.updateById(query.toEntity()));
    }


    @DeleteMapping("{id}")
    @Operation(summary = "删除产品")
    public R<Boolean> delete(@PathVariable Integer id) {
        // 判断能否删除
        if (modelServiceService.existsByProduct((Integer) id)) {
            throw new ServiceException(SysCodeEnum.BAD_REQUEST, "产品下存在物模型信息, 无法删除");
        }
        if (modelPropertyService.existsByProduct((Integer) id)) {
            throw new ServiceException(SysCodeEnum.BAD_REQUEST, "产品下存在物模型信息, 无法删除");
        }
        return R.success(productService.removeById(id));
    }

    @GetMapping("tsl/{productId}")
    @Operation(summary = "物模型")
    public R<TslModel> tslDetails(@PathVariable Integer productId) {
        Product product = productService.getById(productId);
        if (product == null) {
            return R.fail(SysCodeEnum.BAD_REQUEST, "数据不存在");
        }
        return tslModelService.findByProductKey(product.getProductKey())
                .map(R::success).orElse(R.fail("未查询到物模型信息"));
    }


}
