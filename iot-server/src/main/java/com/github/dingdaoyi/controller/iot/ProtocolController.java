package com.github.dingdaoyi.controller.iot;

import com.github.dingdaoyi.entity.Protocol;
import com.github.dingdaoyi.service.ProtocolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import net.dreamlu.mica.core.result.R;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author dingyunwei
 */
@AllArgsConstructor
@RestController
@RequestMapping("/protocol")
@Tag(name = "协议管理")
public class ProtocolController {
    private final ProtocolService protocolService;
    @GetMapping
    @Operation(summary = "协议列表")
    public R<List<Protocol>> list() {
        return R.success(protocolService.list());
    }

}
