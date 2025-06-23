package com.github.dingdaoyi.controller.iot;

import com.github.dingdaoyi.entity.Device;
import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.core.base.PageResult;
import com.github.dingdaoyi.core.enums.ResultCode;
import com.github.dingdaoyi.model.query.DeviceAddQuery;
import com.github.dingdaoyi.model.query.DevicePageQuery;
import com.github.dingdaoyi.model.query.DeviceUpdateQuery;
import com.github.dingdaoyi.model.vo.DevicePageVo;
import com.github.dingdaoyi.model.vo.DeviceVo;
import com.github.dingdaoyi.service.DeviceService;
import com.github.dingdaoyi.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author dingyunwei
 */
@AllArgsConstructor
@RestController
@RequestMapping("/device")
@Tag(name = "设备管理")
public class DeviceController {
    private final ProductService productService;
    private final DeviceService deviceService;

    @GetMapping("{id}")
    @Operation(summary = "设备详情")
    public BaseResult<DeviceVo> details(@PathVariable Integer id) {
        Optional<DeviceVo> optional = deviceService.details(id);
        return optional.map(BaseResult::success).orElse(BaseResult.fail(ResultCode.NOT_FOUND.getCode(), "设备不存在"));
    }

    @PostMapping
    @Operation(summary = "添加设备")
    public BaseResult<Boolean> save(@RequestBody @Valid DeviceAddQuery query) {
        if (!productService.existsById(query.getProductId())) {
            return BaseResult.fail(ResultCode.BAD_REQUEST.getCode(), "产品不存在");
        }
        return BaseResult.success(deviceService.save(query.toEntity()));
    }


    @PutMapping
    @Operation(summary = "修改设备")
    public BaseResult<Boolean> update(@RequestBody @Valid DeviceUpdateQuery query) {
        return BaseResult.success(deviceService.updateById(query.toEntity()));
    }

    @DeleteMapping("{id}")
    @Operation(summary = "修改设备")
    public BaseResult<Boolean> delete(@PathVariable Integer id) {
        return BaseResult.success(deviceService.removeById(id));
    }


    @PostMapping("page")
    @Operation(summary = "设备分页列表")
    public PageResult<DevicePageVo> page(@RequestBody @Valid DevicePageQuery query) {
        return deviceService.pageByQuery(query);
    }
    @GetMapping("list")
    @Operation(summary = "设备编号搜索")
    public BaseResult<List<Device>> list(@RequestParam(required = false) Integer productId,
                             @RequestParam(required = false) Integer productTypeId,
                             @RequestParam(value = "deviceKey",required = false)String deviceKey) {
        return BaseResult.success(deviceService.list(productTypeId,productId,deviceKey));
    }

}
