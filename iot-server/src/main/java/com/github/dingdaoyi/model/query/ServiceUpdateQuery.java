package com.github.dingdaoyi.model.query;

import com.github.dingdaoyi.entity.ModelService;
import com.github.dingdaoyi.model.ToEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 物模型服务修改
 * @author dingyunwei
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ServiceUpdateQuery extends ServiceAddQuery implements ToEntity<ModelService> {


    @Schema(description = "id")
    @NotNull(message = "修改id不能为空")
    private Integer id;


    @Override
    public ModelService toEntity() {
        ModelService entity = super.toEntity();
        entity.setId(id);
        return entity;
    }
}
