package com.github.dingdaoyi.model.query;

import com.baomidou.mybatisplus.annotation.TableField;
import com.github.dingdaoyi.entity.ModelService;
import com.github.dingdaoyi.entity.ServiceProperty;
import com.github.dingdaoyi.entity.enu.ServiceTypeEnum;
import com.github.dingdaoyi.model.ToEntity;
import com.github.dingdaoyi.proto.model.tsl.EventTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.dreamlu.mica.core.utils.$;

import java.util.ArrayList;
import java.util.List;

/**
 * 物模型服务修改
 * @author dingyunwei
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class StandardServiceUpdateQuery extends StandardServiceAddQuery implements ToEntity<ModelService> {


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
