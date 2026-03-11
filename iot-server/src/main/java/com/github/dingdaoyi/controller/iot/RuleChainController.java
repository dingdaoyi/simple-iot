package com.github.dingdaoyi.controller.iot;

import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.core.base.PageResult;
import com.github.dingdaoyi.core.enums.ResultCode;
import com.github.dingdaoyi.entity.Product;
import com.github.dingdaoyi.entity.RuleChain;
import com.github.dingdaoyi.entity.enu.RuleNodeType;
import com.github.dingdaoyi.model.query.RuleChainAddQuery;
import com.github.dingdaoyi.model.query.RuleChainPageQuery;
import com.github.dingdaoyi.model.query.RuleChainUpdateQuery;
import com.github.dingdaoyi.model.vo.RuleChainDetailVo;
import com.github.dingdaoyi.model.vo.RuleChainPageVo;
import com.github.dingdaoyi.proto.model.tsl.TslModel;
import com.github.dingdaoyi.proto.model.tsl.TslProperty;
import com.github.dingdaoyi.service.ProductService;
import com.github.dingdaoyi.service.RuleChainService;
import com.github.dingdaoyi.service.TslModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 规则链控制器
 * @author dingyunwei
 */
@AllArgsConstructor
@RestController
@RequestMapping("/rule-chain")
@Tag(name = "规则链管理")
public class RuleChainController {

    private final RuleChainService ruleChainService;
    private final ProductService productService;
    private final TslModelService tslModelService;

    @GetMapping("{id}")
    @Operation(summary = "规则链详情")
    public BaseResult<RuleChainDetailVo> details(@PathVariable Integer id) {
        return ruleChainService.details(id)
            .map(BaseResult::success)
            .orElse(BaseResult.fail(ResultCode.NOT_FOUND.getCode(), "规则链不存在"));
    }

    @PostMapping
    @Operation(summary = "添加规则链")
    public BaseResult<Boolean> save(@RequestBody @Valid RuleChainAddQuery query) {
        return BaseResult.success(ruleChainService.add(query));
    }

    @PutMapping
    @Operation(summary = "更新规则链")
    public BaseResult<Boolean> update(@RequestBody @Valid RuleChainUpdateQuery query) {
        return BaseResult.success(ruleChainService.updateByQuery(query));
    }

    @DeleteMapping("{id}")
    @Operation(summary = "删除规则链")
    public BaseResult<Boolean> delete(@PathVariable Integer id) {
        return BaseResult.success(ruleChainService.removeById(id));
    }

    @PostMapping("page")
    @Operation(summary = "规则链分页列表")
    public PageResult<RuleChainPageVo> page(@RequestBody @Valid RuleChainPageQuery query) {
        return ruleChainService.pageByQuery(query);
    }

    @PutMapping("{id}/enable")
    @Operation(summary = "启用/禁用规则链")
    public BaseResult<Boolean> toggleEnable(@PathVariable Integer id, @RequestParam Boolean enabled) {
        RuleChain ruleChain = new RuleChain();
        ruleChain.setId(id);
        ruleChain.setIsEnabled(enabled);
        return BaseResult.success(ruleChainService.updateById(ruleChain));
    }

    @GetMapping("node-types")
    @Operation(summary = "获取节点类型列表")
    public BaseResult<Map<String, List<NodeTypeInfo>>> getNodeTypes() {
        Map<String, List<NodeTypeInfo>> result = Arrays.stream(RuleNodeType.values())
            .collect(Collectors.groupingBy(
                type -> type.getCategory().name(),
                Collectors.mapping(NodeTypeInfo::new, Collectors.toList())
            ));
        return BaseResult.success(result);
    }

    /**
     * 节点类型信息
     */
    @lombok.Data
    public static class NodeTypeInfo {
        private String type;
        private String name;
        private String description;
        private String category;
        private String color;

        public NodeTypeInfo(RuleNodeType nodeType) {
            this.type = nodeType.name();
            this.name = nodeType.getName();
            this.description = nodeType.getDescription();
            this.category = nodeType.getCategory().name();
            this.color = nodeType.getCategory().getColor();
        }
    }

    /**
     * 模板变量信息
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class TemplateVariableInfo {
        private String variable;    // 变量名，如 ${deviceName}
        private String name;        // 显示名称
        private String description; // 描述
        private String category;    // 分类: SYSTEM-系统变量, PROPERTY-属性变量
    }

    @GetMapping("template-variables")
    @Operation(summary = "获取模板变量列表")
    public BaseResult<List<TemplateVariableInfo>> getTemplateVariables(
            @RequestParam(required = false) Integer productId) {
        List<TemplateVariableInfo> variables = new ArrayList<>();

        // 1. 系统固定变量
        variables.add(new TemplateVariableInfo("${deviceKey}", "设备Key", "设备的唯一标识符", "SYSTEM"));
        variables.add(new TemplateVariableInfo("${deviceName}", "设备名称", "设备的名称", "SYSTEM"));
        variables.add(new TemplateVariableInfo("${deviceId}", "设备ID", "设备的数字ID", "SYSTEM"));
        variables.add(new TemplateVariableInfo("${eventTime}", "事件时间", "消息触发的时间", "SYSTEM"));

        // 2. 物模型属性变量
        if (productId != null) {
            Product product = productService.getById(productId);
            if (product != null && product.getProductKey() != null) {
                tslModelService.findByProductKey(product.getProductKey())
                    .ifPresent(tslModel -> {
                        if (tslModel.getProperties() != null) {
                            for (TslProperty property : tslModel.getProperties()) {
                                String identifier = property.getIdentifier();
                                String name = property.getName();
                                String desc = String.format("%s (%s)", name,
                                    property.getDataType() != null ? property.getDataType().getName() : "");
                                variables.add(new TemplateVariableInfo("${" + identifier + "}", name, desc, "PROPERTY"));
                            }
                        }
                    });
            }
        }

        return BaseResult.success(variables);
    }
}
