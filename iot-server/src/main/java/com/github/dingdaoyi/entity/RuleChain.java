package com.github.dingdaoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.github.dingdaoyi.core.base.BaseEntity;
import com.github.dingdaoyi.entity.enu.RuleSourceType;
import com.github.dingdaoyi.rule.config.NodeConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * 规则链实体
 * @author dingyunwei
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "tb_rule_chain", autoResultMap = true)
@Schema(description = "规则链")
public class RuleChain extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "规则链ID")
    private Integer id;

    @TableField(value = "name")
    @Schema(description = "规则链名称")
    private String name;

    @TableField(value = "description")
    @Schema(description = "描述")
    private String description;

    @TableField(value = "is_root")
    @Schema(description = "是否为根规则链")
    private Boolean isRoot;

    @TableField(value = "is_enabled")
    @Schema(description = "是否启用")
    private Boolean isEnabled;

    @TableField(value = "source_type")
    @Schema(description = "数据源类型")
    private RuleSourceType sourceType;

    @TableField(value = "source_id")
    @Schema(description = "数据源ID")
    private Integer sourceId;

    @TableField(value = "configuration", typeHandler = JacksonTypeHandler.class)
    @Schema(description = "节点和连接配置")
    private RuleChainConfiguration configuration;

    /**
     * 规则链配置
     */
    @Data
    @Schema(description = "规则链配置")
    public static class RuleChainConfiguration {
        @Schema(description = "节点列表")
        private List<RuleNode> nodes;

        @Schema(description = "连接列表")
        private List<RuleConnection> connections;
    }

    /**
     * 规则节点
     */
    @Data
    @Schema(description = "规则节点")
    public static class RuleNode {
        @Schema(description = "节点ID")
        private String id;

        @Schema(description = "节点类型")
        private String type;

        @Schema(description = "节点名称")
        private String name;

        @Schema(description = "X坐标")
        private Integer x;

        @Schema(description = "Y坐标")
        private Integer y;

        @Schema(description = "节点配置")
        private NodeConfig config;
    }

    /**
     * 节点连接
     */
    @Data
    @Schema(description = "节点连接")
    public static class RuleConnection {
        @Schema(description = "源节点ID")
        private String source;

        @Schema(description = "目标节点ID")
        private String target;

        @Schema(description = "连接类型: Success, Failure, True, False")
        private String type;
    }
}
