package com.github.dingdaoyi.model.query;

import com.github.dingdaoyi.entity.IotRule;
import com.github.dingdaoyi.entity.enu.RuleInputType;
import com.github.dingdaoyi.entity.enu.RuleSourceType;
import com.github.dingdaoyi.entity.enu.RuleTargetType;
import com.github.dingdaoyi.entity.enu.RuleType;
import com.github.dingdaoyi.model.ToEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author dingyunwei
 */
@Data
public class RuleUpdateQuery implements ToEntity<IotRule> {

    @Schema(description = "规则id")
    private Integer id;


    /**
     * 规则名称
     */
    @Schema(description = "规则名称")
    private String name;



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
        iotRule.setId(this.id);
        iotRule.setName(name);
        iotRule.setScript(script);
        iotRule.setRemark(remark);
        iotRule.setTargetType(targetType);
        iotRule.setTargetId(targetId);
        return iotRule;
    }
}
