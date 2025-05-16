package com.github.dingdaoyi.model.vo;


import cn.hutool.core.bean.BeanUtil;
import com.github.dingdaoyi.entity.ModelService;
import com.github.dingdaoyi.proto.model.tsl.TslProperty;
import com.github.dingdaoyi.proto.model.tsl.TslService;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author dingyunwei
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ModelServiceVO extends ModelService {
    @Schema(description = "出参")
    private List<Integer> outputParamIds;

    @Schema(description = "服务输入参数")
    private List<Integer> inputParamIds;

    public static ModelServiceVO build(ModelService modelService) {
        ModelServiceVO modelServiceVO = new ModelServiceVO();
        BeanUtil.copyProperties(modelService, modelServiceVO);
        return modelServiceVO;
    }

    public TslService toTsl(List<TslProperty> inputParams, List<TslProperty> outputParams) {
        TslService tslService = new TslService(inputParams, outputParams);
        tslService.setId(this.getId());
        tslService.setAsync(this.getAsync());
        tslService.setIdentifier(this.getIdentifier());
        tslService.setName(this.getName());
        tslService.setRemark(this.getRemark());
        tslService.setRequired(this.getRequired());
        return tslService;
    }
}
