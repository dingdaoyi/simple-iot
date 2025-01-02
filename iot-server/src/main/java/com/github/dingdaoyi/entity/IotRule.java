package com.github.dingdaoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.dingdaoyi.entity.enu.RuleInputType;
import com.github.dingdaoyi.entity.enu.RuleSourceType;
import com.github.dingdaoyi.entity.enu.RuleTargetType;
import com.github.dingdaoyi.entity.enu.RuleType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author dingyunwei
 */
@Schema
@Data
@TableName(value = "tb_iot_rule")
public class IotRule {
    /**
     * 规则id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "规则id")
    private Integer id;

    /**
     * 规则名称
     */
    @TableField(value = "\"name\"")
    @Schema(description = "规则名称")
    private String name;

    /**
     * 处理类型
     */
    @TableField(value = "rule_type")
    @Schema(description = "处理类型")
    private RuleType ruleType;

    /**
     * 处理脚本
     */
    @TableField(value = "script")
    @Schema(description = "处理脚本")
    private String script;

    /**
     * 备注
     */
    @TableField(value = "remark")
    @Schema(description = "备注")
    private String remark;

    /**
     * 输入类型
     */
    @TableField(value = "input_type")
    @Schema(description = "输入类型")
    private RuleInputType inputType;

    @TableField(value = "identifier")
    @Schema(description = "标识符")
    private String identifier;
    /**
     * 源数据类型,1,产品;2,设备分组,3, 特定设备
     */
    @TableField(value = "source_type")
    @Schema(description = "源数据类型,1,产品;2,设备分组,3, 特定设备")
    private RuleSourceType sourceType;

    /**
     * 数据源id
     */
    @TableField(value = "source_id")
    @Schema(description = "数据源id")
    private Integer sourceId;

    /**
     * 目标类型, 1: http, 2,mqtt; 3,message
     */
    @TableField(value = "target_type")
    @Schema(description = "目标类型, 1: http, 2,mqtt; 3,message")
    private RuleTargetType targetType;

    /**
     * 目标id
     */
    @TableField(value = "target_id")
    @Schema(description = "目标id")
    private Integer targetId;

    /**
     * 解析
     * @param value 参数
     * @return
     */
    public Object parse(Object value) {
      return   this.getRuleType().ruleProcessor.parse(value,this.script);
    }
}