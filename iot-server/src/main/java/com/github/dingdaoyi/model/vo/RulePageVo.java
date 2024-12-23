package com.github.dingdaoyi.model.vo;

import com.github.dingdaoyi.entity.IotRule;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingyunwei
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RulePageVo extends IotRule {

    private String sourceName;

}
