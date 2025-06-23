package com.github.dingdaoyi.controller.iot;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.dingdaoyi.entity.Product;
import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author dingyunwei
 */
@AllArgsConstructor
@RestController
@RequestMapping("/manufacturer")
@Tag(name = "厂家控制器:(暂时不设单独厂家管理)")
public class ManufacturerController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "查找厂家列表")
    public BaseResult<List<String>> list(@RequestParam Integer productTypeId) {
        List<String> dictList = productService.listObjs(Wrappers
                .lambdaQuery(Product.class)
                .eq(Product::getProductTypeId, productTypeId)
                .select(Product::getManufacturer));
        return BaseResult.success(dictList);
    }

}
