package com.github.dingdaoyi.controller.iot;

import com.github.dingdaoyi.model.enu.SysCodeEnum;
import com.github.dingdaoyi.model.query.StandardServiceAddQuery;
import com.github.dingdaoyi.model.vo.ModelServiceVO;
import com.github.dingdaoyi.service.ModelPropertyService;
import com.github.dingdaoyi.service.ModelServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import net.dreamlu.mica.core.exception.ServiceException;
import net.dreamlu.mica.core.result.R;
import net.dreamlu.mica.core.utils.$;
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
                                              @RequestParam(required = false) Integer status,
                                              @RequestParam Integer productTypeId) {
        return R.success(modelServiceService.listByProductType(productTypeId,serviceType,status));
    }

    @PutMapping("standard/enable")
    @Operation(summary = "启用,禁用标准物模型")
    public R<Boolean> updateServiceStatus(
            @Parameter(description = "启用,禁用状态,1,启用,0 禁用", required = true, example = "1")
            @Min(value = 0, message = "状态只能是0或1")
            @Max(value = 1, message = "状态只能是0或1")  @RequestParam Integer status,
            @RequestParam Integer serviceId) {
        return R.success(modelServiceService.updateServiceStatus(serviceId,status));
    }



    @PostMapping("standard")
    @Operation(summary = "新增标准物模型")
    public R<Boolean> save(@RequestBody StandardServiceAddQuery modelService) {
        validateProperties(modelService);
        return R.success(modelServiceService.save(modelService));
    }

    private void validateProperties(StandardServiceAddQuery modelService) {
        if ($.isNotEmpty(modelService.getInputParamIds())) {
            if (!modelPropertyService.allExists(modelService.getInputParamIds())) {
                throw new ServiceException(SysCodeEnum.BAD_REQUEST,"请确保入参id");
            }
        }
    }

}
