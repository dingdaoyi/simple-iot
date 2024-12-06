package com.github.dingdaoyi.controller.system;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.dingdaoyi.entity.Icon;
import com.github.dingdaoyi.service.IconService;
import com.github.dingdaoyi.service.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import net.dreamlu.mica.core.result.R;
import net.dreamlu.mica.core.utils.$;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
                        .like($.isNotBlank(name), Icon::getName, name)
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
