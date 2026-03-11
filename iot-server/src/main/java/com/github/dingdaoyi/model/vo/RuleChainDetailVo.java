package com.github.dingdaoyi.model.vo;

import com.github.dingdaoyi.entity.RuleChain;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 规则链详情VO
 * @author dingyunwei
 */
@Data
@Schema(description = "规则链详情VO")
public class RuleChainDetailVo {

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
    private String sourceType;

    @Schema(description = "数据源ID")
    private Integer sourceId;

    @Schema(description = "数据源名称")
    private String sourceName;

    @Schema(description = "节点和连接配置")
    private RuleChain.RuleChainConfiguration configuration;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
