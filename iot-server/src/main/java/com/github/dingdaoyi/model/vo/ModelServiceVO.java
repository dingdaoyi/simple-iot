package com.github.dingdaoyi.model.vo;


import com.github.dingdaoyi.entity.ModelService;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.dreamlu.mica.core.utils.$;

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
        $.copy(modelService, modelServiceVO);
        return modelServiceVO;
    }
}
