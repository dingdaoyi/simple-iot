package com.github.dingdaoyi.controller.iot;

import com.github.dingdaoyi.model.PageResult;
import com.github.dingdaoyi.model.query.*;
import com.github.dingdaoyi.model.vo.RulePageVo;
import com.github.dingdaoyi.service.IotRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import net.dreamlu.mica.core.result.R;
import org.springframework.web.bind.annotation.*;

/**
 * @author dingyunwei
 */
@AllArgsConstructor
@RestController
@RequestMapping("/rule")
@Tag(name = "规则管理")
public class RuleController {
    private final IotRuleService ruleService;

    @PostMapping("page")
    @Operation(summary = "分页查询")
    public PageResult<RulePageVo> page(@RequestBody RulePageQuery  pageQuery) {
       return ruleService.pageByQuery(pageQuery);
    }

    @PostMapping
    @Operation(summary = "添加")
    public R<Boolean> save(@RequestBody RuleAddQuery addQuery) {
        return R.success(ruleService.save(addQuery));
    }
}
