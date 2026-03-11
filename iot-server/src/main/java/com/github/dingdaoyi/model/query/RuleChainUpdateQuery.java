package com.github.dingdaoyi.model.query;

import com.github.dingdaoyi.entity.RuleChain;
import com.github.dingdaoyi.entity.enu.RuleSourceType;
import com.github.dingdaoyi.model.ToEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 规则链更新请求
 * @author dingyunwei
 */
@Data
@Schema(description = "规则链更新请求")
public class RuleChainUpdateQuery implements ToEntity<RuleChain> {

    @NotNull(message = "规则链ID不能为空")
    @Schema(description = "规则链ID")
    private Integer id;

    @Schema(description = "规则链名称")
    private String name;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "是否为根规则链")
    private Boolean isRoot;

    @Schema(description = "是否启用")
    private Boolean isEnabled;

    @Schema(description = "数据源类型")
    private RuleSourceType sourceType;

    @Schema(description = "数据源ID")
    private Integer sourceId;

    @Schema(description = "节点和连接配置")
    private RuleChain.RuleChainConfiguration configuration;

    @Override
    public RuleChain toEntity() {
        RuleChain entity = new RuleChain();
        entity.setId(id);
        entity.setName(name);
        entity.setDescription(description);
        entity.setIsRoot(isRoot);
        entity.setIsEnabled(isEnabled);
        entity.setSourceType(sourceType);
        entity.setSourceId(sourceId);
        entity.setConfiguration(configuration);
        return entity;
    }
}
