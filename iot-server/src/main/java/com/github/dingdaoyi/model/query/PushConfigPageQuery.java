package com.github.dingdaoyi.model.query;

import com.github.dingdaoyi.entity.enu.PushConfigType;
import com.github.dingdaoyi.model.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 推送配置分页查询
 * @author dingyunwei
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "推送配置分页查询")
public class PushConfigPageQuery extends PageQuery {

    @Schema(description = "配置名称")
    private String name;

    @Schema(description = "配置类型")
    private PushConfigType type;

    @Schema(description = "是否启用")
    private Boolean isEnabled;
}
