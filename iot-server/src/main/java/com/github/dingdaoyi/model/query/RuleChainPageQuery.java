package com.github.dingdaoyi.model.query;

import com.github.dingdaoyi.entity.enu.RuleSourceType;
import com.github.dingdaoyi.model.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 规则链分页查询
 * @author dingyunwei
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "规则链分页查询")
public class RuleChainPageQuery extends PageQuery {

    @Schema(description = "规则链名称")
    private String name;

    @Schema(description = "数据源类型")
    private RuleSourceType sourceType;

    @Schema(description = "是否启用")
    private Boolean isEnabled;
}
