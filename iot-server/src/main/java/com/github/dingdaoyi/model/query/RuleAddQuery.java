package com.github.dingdaoyi.model.query;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.github.dingdaoyi.entity.Device;
import com.github.dingdaoyi.entity.IotRule;
import com.github.dingdaoyi.entity.enu.RuleInputType;
import com.github.dingdaoyi.entity.enu.RuleSourceType;
import com.github.dingdaoyi.entity.enu.RuleTargetType;
import com.github.dingdaoyi.entity.enu.RuleType;
import com.github.dingdaoyi.model.ToEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author dingyunwei
 */
@Data
public class RuleAddQuery implements ToEntity<IotRule> {

    /**
     * 规则名称
     */
    @Schema(description = "规则名称")
    private String name;

    /**
     * 处理类型
     */
    @Schema(description = "处理类型")
    private RuleType ruleType;

    /**
     * 处理脚本
     */
    @Schema(description = "处理脚本")
    private String script;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 输入类型
     */
    @Schema(description = "输入类型")
    private RuleInputType inputType;

    /**
     * 源数据类型,1,产品;2,设备分组,3, 特定设备
     */
    @Schema(description = "源数据类型,1,产品;2,设备分组,3, 特定设备")
    private RuleSourceType sourceType;

    /**
     * 数据源id
     */
    @Schema(description = "数据源id")
    private Integer sourceId;

    /**
     * 目标类型, 1: http, 2,mqtt; 3,message
     */
    @Schema(description = "目标类型, 1: http, 2,mqtt; 3,message")
    private RuleTargetType targetType;

    /**
     * 目标id
     */
    @Schema(description = "目标id")
    private Integer targetId;

    @Override
    public IotRule toEntity() {
        IotRule iotRule = new IotRule();
        iotRule.setName(name);
        iotRule.setRuleType(ruleType);
        iotRule.setScript(script);
        iotRule.setRemark(remark);
        iotRule.setInputType(inputType);
        iotRule.setSourceType(sourceType);
        iotRule.setSourceId(sourceId);
        iotRule.setTargetType(targetType);
        iotRule.setTargetId(targetId);
        return iotRule;
    }
}
