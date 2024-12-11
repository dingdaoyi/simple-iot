package com.github.dingdaoyi.controller.iot;

import com.github.dingdaoyi.model.PageResult;
import com.github.dingdaoyi.model.enu.SysCodeEnum;
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
import net.dreamlu.mica.core.result.R;
import org.springframework.web.bind.annotation.*;

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
    public R<DeviceVo> details(@PathVariable Integer id) {
        Optional<DeviceVo> optional = deviceService.details(id);
        return optional.map(R::success).orElse(R.fail(SysCodeEnum.BAD_REQUEST, "设备不存在"));
    }

    @PostMapping
    @Operation(summary = "添加设备")
    public R<Boolean> save(@RequestBody @Valid DeviceAddQuery query) {
        if (!productService.existsById(query.getProductId())) {
            return R.fail(SysCodeEnum.BAD_REQUEST, "产品不存在");
        }
        return R.success(deviceService.save(query.toEntity()));
    }


    @PutMapping
    @Operation(summary = "修改设备")
    public R<Boolean> update(@RequestBody @Valid DeviceUpdateQuery query) {
        return R.success(deviceService.updateById(query.toEntity()));
    }

    @DeleteMapping("{id}")
    @Operation(summary = "修改设备")
    public R<Boolean> update(@PathVariable Integer id) {
        return R.success(deviceService.removeById(id));
    }


    @PostMapping("page")
    @Operation(summary = "设备分页列表")
    public PageResult<DevicePageVo> page(@RequestBody @Valid DevicePageQuery query) {
        return deviceService.pageByQuery(query);
    }

}
