package com.github.dingdaoyi.controller.system;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.dingdaoyi.entity.Icon;
import com.github.dingdaoyi.model.base.R;
import com.github.dingdaoyi.service.IconService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author dingyunwei
 */
@RestController
@Tag(name = "图标管理")
@RequestMapping("icon")
public class IconController {

    @Resource
    private IconService iconService;

    @Operation(summary = "图标列表")
    @GetMapping
    public R<List<Icon>> list(@RequestParam(required = false) String name) {
        return R.success(iconService.list(
                Wrappers
                        .<Icon>lambdaQuery()
                        .like(StringUtils.isNotBlank(name), Icon::getName, name)
        ));
    }

    @Operation(summary = "图标添加")
    @PostMapping
    public R<Boolean> save(@RequestBody Icon icon) {
        return R.success(iconService.save(icon));
    }

    @Operation(summary = "图标修改")
    @PutMapping
    public R<Boolean> update(@RequestBody Icon icon) {
        if (icon.getId() == null) {
            return R.fail("图标id为空,请在页面编辑");
        }
        return R.success(iconService.updateById(icon));
    }

    @Operation(summary = "图标删除")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Integer id) {
        return R.success(iconService.removeById(id));
    }
}
