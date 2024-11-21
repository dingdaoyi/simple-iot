package com.github.dingdaoyi.controller.iot;

import com.github.dingdaoyi.entity.ProductType;
import com.github.dingdaoyi.model.PageQuery;
import com.github.dingdaoyi.model.PageResult;
import com.github.dingdaoyi.model.query.ProductTypeAddQuery;
import com.github.dingdaoyi.service.ProductTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import net.dreamlu.mica.core.result.R;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author dingyunwei
 */
@AllArgsConstructor
@RestController
@RequestMapping("/product/type")
@Tag(name = "产品类型")
public class ProductTypeController {
    private final ProductTypeService productTypeService;

    @GetMapping
    @Operation(summary = "获取产品类型")
    public R<List<ProductType>> list(@RequestParam(defaultValue = "-1") Integer parentId) {
        return R.success(productTypeService.listByParentId(parentId));
    }

    @PostMapping("page")
    @Operation(summary = "获取产品类型")
    public PageResult<ProductType> page(@RequestBody PageQuery query) {
        return productTypeService.pageByQuery(query);
    }


    @PostMapping
    @Operation(summary = "添加产品类型")
    public R<Boolean> save(@RequestBody @Valid ProductTypeAddQuery query) {
        return R.success(productTypeService.add(query.toEntity()));
    }

    @PutMapping("status/{id}")
    @Operation(summary = "修改产品类型状态")
    public R<Boolean> updateStatus(@RequestParam @Min(value = 1, message = "状态必须是1和2")
                                   @Max(value = 2, message = "状态必须是1和2")
                                   Integer status,
                                   @PathVariable Integer id) {
        return R.success(productTypeService.updateStatusById(status, id));
    }

}
