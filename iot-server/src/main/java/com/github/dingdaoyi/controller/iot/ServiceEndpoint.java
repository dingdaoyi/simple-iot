package com.github.dingdaoyi.controller.iot;

import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.core.exception.BusinessException;
import com.github.dingdaoyi.service.ServiceHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author dwx
 * @date 2021/6/1 0001
 */
@RestController
@RequestMapping("service")
@Slf4j
@AllArgsConstructor
@Tag(name = "设备控制服务")
public class ServiceEndpoint {
    private ServiceHandler serviceHandler;

    //TODO 指令下发记录日志如果错误需要知道原因
    @PostMapping("/{deviceKey}/{identifier}")
    @Operation(summary = "调用设备服务")
    public BaseResult<Map<String, Object>> sendMessage(
            @Parameter(name = "设备编号") @PathVariable String deviceKey,
            @Parameter(name = "标识符") @PathVariable String identifier,
            @Parameter(name = "参数列表") @RequestBody Map<String, Object> paramMap) throws BusinessException {

        final Map<String, Object> message;
        try {
            log.info("COMMAND_REQUEST_PARAMETERS|命令下发,请求参数|{}|{}|{}", deviceKey, identifier, paramMap);
            message = serviceHandler.sendMessage(deviceKey, identifier, paramMap);
            log.info("INSTRUCTION_SENT_SUCCESSFULLY|指令下发成功|{}|{}|{}", deviceKey, identifier, message);
        } catch (BusinessException e) {
            log.info("INSTRUCTION_SENT_FAILURE|指令下发失败|{}|{}|{}", deviceKey, identifier,e.getMessage());
            return BaseResult.fail(e.getMessage());
        }
        return BaseResult.success(message);
    }

    /**
     * 批量下发设备指令
     * @author dingyunwei
     */
    @PostMapping("/batch/{identifier}")
    @Operation(summary = "批量下发设备指令")
    public BaseResult<Map<String, Object>> batchSend(
            @Parameter(name = "标识符") @PathVariable String identifier,
            @RequestBody Map<String, Object> body) {

        Object deviceKeysObj = body.get("deviceKeys");
        if (!(deviceKeysObj instanceof List<?> rawList)) {
            return BaseResult.fail("deviceKeys 必须是数组");
        }
        @SuppressWarnings("unchecked")
        List<String> deviceKeys = (List<String>) (List<?>) rawList;
        if (deviceKeys.isEmpty()) {
            return BaseResult.fail("deviceKeys 不能为空");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> paramMap = (Map<String, Object>) body.getOrDefault("params", new HashMap<>());

        int success = 0, failed = 0;
        List<Map<String, Object>> results = new ArrayList<>();

        for (String dk : deviceKeys) {
            Map<String, Object> r = new LinkedHashMap<>();
            r.put("deviceKey", dk);
            try {
                Map<String, Object> msg = serviceHandler.sendMessage(dk, identifier, paramMap);
                r.put("status", "SUCCESS");
                r.put("data", msg);
                success++;
            } catch (Exception e) {
                r.put("status", "FAILED");
                r.put("error", e.getMessage());
                failed++;
            }
            results.add(r);
        }

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("total", deviceKeys.size());
        summary.put("success", success);
        summary.put("failed", failed);
        summary.put("results", results);
        return BaseResult.success(summary);
    }
}
