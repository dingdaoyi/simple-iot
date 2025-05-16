package com.github.dingdaoyi.controller.system;

import com.github.dingdaoyi.entity.Dict;
import com.github.dingdaoyi.model.base.R;
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
    public R<List<Dict>> groupList() {
        List<Dict> list = dictService.groupList();
        return R.success(list);
    }

    @GetMapping("list")
    @Operation(summary  = "查询所有类型的字典")
    public R<List<Dict>> getList(@RequestParam String group) {
        return R.success(dictService.listByCode(group));
    }
}
