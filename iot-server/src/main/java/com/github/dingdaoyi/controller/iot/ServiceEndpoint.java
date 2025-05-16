package com.github.dingdaoyi.controller.iot;

import com.github.dingdaoyi.model.base.R;
import com.github.dingdaoyi.model.exception.ServiceException;
import com.github.dingdaoyi.service.ServiceHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @PostMapping("/{deviceKey}/{identifier}")
    @Operation(summary = "调用设备服务")
    public R<Map<String, Object>> sendMessage(
            @Parameter(name = "设备编号") @PathVariable String deviceKey,
            @Parameter(name = "标识符") @PathVariable String identifier,
            @Parameter(name = "参数列表") @RequestBody Map<String, Object> paramMap) throws ServiceException {

        final Map<String, Object> message;
        try {
            log.info("COMMAND_REQUEST_PARAMETERS|命令下发,请求参数|{}|{}|{}", deviceKey, identifier, paramMap);
            message = serviceHandler.sendMessage(deviceKey, identifier, paramMap);
            log.info("INSTRUCTION_SENT_SUCCESSFULLY|指令下发成功|{}|{}|{}", deviceKey, identifier, message);
        } catch (ServiceException e) {
            log.info("INSTRUCTION_SENT_FAILURE|指令下发失败|{}|{}|{}", deviceKey, identifier,e.getMessage());
            throw e;
        }
        return R.success(message);
    }
}
