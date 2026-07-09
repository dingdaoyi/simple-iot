package com.github.dingdaoyi.controller.iot;

import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.entity.PushLog;
import com.github.dingdaoyi.mapper.PushLogMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/push-config")
@Tag(name = "推送配置管理")
public class PushLogController {

    private final PushLogMapper pushLogMapper;

    @GetMapping("{pushConfigId}/logs")
    @Operation(summary = "推送日志列表（最近20条）")
    public BaseResult<List<PushLog>> logs(@PathVariable Integer pushConfigId) {
        var wrapper = new LambdaQueryWrapper<PushLog>()
                .eq(PushLog::getPushConfigId, pushConfigId)
                .orderByDesc(PushLog::getCreateTime)
                .last("LIMIT 20");
        return BaseResult.success(pushLogMapper.selectList(wrapper));
    }
}
