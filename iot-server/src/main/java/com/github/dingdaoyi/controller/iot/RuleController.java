package com.github.dingdaoyi.controller.iot;

import com.github.dingdaoyi.core.base.PageResult;
import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.model.query.*;
import com.github.dingdaoyi.model.vo.RuleDetailVo;
import com.github.dingdaoyi.model.vo.RulePageVo;
import com.github.dingdaoyi.service.IotRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * @author dingyunwei
 */
@RestController
@RequestMapping("/rule")
@Tag(name = "规则管理")
public class RuleController {
    @Resource
    private IotRuleService ruleService;

    @PostMapping("page")
    @Operation(summary = "分页查询")
    public PageResult<RulePageVo> page(@RequestBody RulePageQuery pageQuery) {
        return ruleService.pageByQuery(pageQuery);
    }

    @GetMapping("/{id}")
    @Operation(summary = "分页查询")
    public BaseResult<RuleDetailVo> details(@PathVariable Integer id) {
        return BaseResult.success(ruleService.details(id));
    }


    @PostMapping
    @Operation(summary = "添加")
    public BaseResult<Boolean> save(@RequestBody RuleAddQuery addQuery) {
        return BaseResult.success(ruleService.save(addQuery));
    }

    @PutMapping
    @Operation(summary = "修改")
    public BaseResult<Boolean> update(@RequestBody RuleUpdateQuery updateQuery) {
        return BaseResult.success(ruleService.update(updateQuery));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    public BaseResult<Boolean> delete(@PathVariable Integer id) {
        return BaseResult.success(ruleService.removeById(id));
    }
}
