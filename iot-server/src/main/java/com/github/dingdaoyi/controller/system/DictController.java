package com.github.dingdaoyi.controller.system;

import com.github.dingdaoyi.entity.Dict;
import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.service.DictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * @author hasee
 */
@RestController
@Tag(name = "字典管理")
@RequestMapping("/dict")
public class DictController{

    @Resource
    private DictService dictService;


    @GetMapping("groups")
    @Operation(summary  = "查询所有类型的字典")
    public BaseResult<List<Dict>> groupList() {
        List<Dict> list = dictService.groupList();
        return BaseResult.success(list);
    }

    @GetMapping("list")
    @Operation(summary  = "查询所有类型的字典")
    public BaseResult<List<Dict>> getList(@RequestParam String group) {
        return BaseResult.success(dictService.listByCode(group));
    }
}
