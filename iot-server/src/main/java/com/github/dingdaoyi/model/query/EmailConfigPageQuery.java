package com.github.dingdaoyi.model.query;

import com.github.dingdaoyi.model.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 邮箱配置分页查询
 * @author dingyunwei
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "邮箱配置分页查询")
public class EmailConfigPageQuery extends PageQuery {

    @Schema(description = "配置名称")
    private String name;

    @Schema(description = "状态 1启用 2禁用")
    private Integer status;
}