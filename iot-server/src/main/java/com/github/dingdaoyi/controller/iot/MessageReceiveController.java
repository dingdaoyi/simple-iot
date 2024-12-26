package com.github.dingdaoyi.controller.iot;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.dingdaoyi.entity.MessageReceive;
import com.github.dingdaoyi.entity.Product;
import com.github.dingdaoyi.model.PageResult;
import com.github.dingdaoyi.model.enu.NotifyType;
import com.github.dingdaoyi.model.enu.SysCodeEnum;
import com.github.dingdaoyi.model.query.*;
import com.github.dingdaoyi.model.vo.ProductPageVo;
import com.github.dingdaoyi.service.MessageReceiveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import net.dreamlu.mica.core.exception.ServiceException;
import net.dreamlu.mica.core.result.R;
import net.dreamlu.mica.core.utils.$;
import org.springframework.web.bind.annotation.*;

import java.sql.Wrapper;
import java.util.List;

/**
 * @author dingyunwei
 */
@RestController
@RequestMapping("/message/receive")
@Tag(name = "规则管理")
public class MessageReceiveController {
    @Resource
    private MessageReceiveService messageReceiveService;

    @GetMapping
    @Operation(summary = "根据产品类型和厂家获取")
    public R<List<MessageReceive>> list(@RequestParam(required = false)  String name) {
        return R.success(messageReceiveService.list(Wrappers
                .<MessageReceive>lambdaQuery()
                .like($.isNotBlank(name), MessageReceive::getName, name)));
    }

    @PostMapping("page")
    @Operation(summary = "分页获取")
    public PageResult<MessageReceive> page(@RequestBody MessageRecivePageQuery pageQuery) {
        return messageReceiveService.pageByQuery(pageQuery);
    }

    @PostMapping
    @Operation(summary = "添加")
    public R<Boolean> save(@RequestBody @Valid MessageReceiveAddQuery query) {
        return R.success(messageReceiveService.save(query.toEntity()));
    }

    @PutMapping
    @Operation(summary = "修改")
    public R<Boolean> update(@RequestBody @Valid MessageReceiveUpdateQuery query) {
        return R.success(messageReceiveService.updateById(query.toEntity()));
    }


    @DeleteMapping("{id}")
    @Operation(summary = "删除产品")
    public R<Boolean> delete(@PathVariable Integer id) {
        return R.success(messageReceiveService.deleteById(id));
    }

}
