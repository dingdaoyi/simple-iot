package com.github.dingdaoyi.model.vo;

import cn.hutool.core.bean.BeanUtil;
import com.github.dingdaoyi.entity.IotRule;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingyunwei
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RuleDetailVo extends IotRule {


    /**
     * 产品类型id
     */
    private Integer productTypeId;

    /**
     * 产品类型名称
     */
    private String productTypeName;
    /**
     * 产品id
     */
    private Integer productId;

    /**
     * 产品名称(型号(厂家))
     */
    private String productName;

    /**
     * 接收者名称
     */
    private String targetName;

    /**
     * 源名称
     */
    private String sourceName;

    public RuleDetailVo(IotRule iotRule) {
        BeanUtil.copyProperties(iotRule, this);
    }
}
