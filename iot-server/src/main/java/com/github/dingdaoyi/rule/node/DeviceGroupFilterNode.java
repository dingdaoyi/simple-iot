package com.github.dingdaoyi.rule.node;

import com.github.dingdaoyi.entity.enu.RuleNodeType;
import com.github.dingdaoyi.rule.RuleContext;
import com.github.dingdaoyi.rule.RuleNodeExecutor;
import com.github.dingdaoyi.rule.config.DeviceGroupFilterConfig;
import com.github.dingdaoyi.rule.config.NodeConfig;
import com.github.dingdaoyi.service.DeviceGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 设备分组过滤节点
 * 只允许指定分组下的设备数据通过
 */
@Component
@RequiredArgsConstructor
public class DeviceGroupFilterNode implements RuleNodeExecutor {

    private final DeviceGroupService deviceGroupService;

    @Override
    public RuleNodeType getNodeType() {
        return RuleNodeType.FILTER_DEVICE_GROUP;
    }

    @Override
    public NodeResult execute(RuleContext context, NodeConfig config) {
        if (!(config instanceof DeviceGroupFilterConfig cfg)) {
            return NodeResult.fail("配置类型错误");
        }
        if (cfg.getGroupId() == null) {
            return NodeResult.fail("未配置分组");
        }

        Integer deviceId = context.getDeviceId();
        if (deviceId == null) {
            return NodeResult.fail("设备ID为空，无法匹配分组");
        }

        Set<Integer> groupDeviceIds = deviceGroupService.listDeviceIds(cfg.getGroupId())
                .stream()
                .collect(Collectors.toSet());

        if (groupDeviceIds.contains(deviceId)) {
            return NodeResult.ok();
        }
        return NodeResult.of(false, "通过", "设备不在分组中");
    }
}
