package com.github.dingdaoyi.controller.system;

import com.github.dingdaoyi.service.impl.EmailNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import net.dreamlu.mica.core.result.R;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 *
 * MessageReceive
 * @author dingyunwei
 */
@RestController
@Tag(name = "消息通知")
@RequestMapping("notification")
public class NotificationController {
    @Resource
    private EmailNotificationService notificationService;

    @Operation(summary = "发送消息")
    @GetMapping
    public R<Boolean> sendTest() {
        notificationService.sendMessage("1493001032@qq.com", "event_notification",
                Map.of("deviceName", "烟雾报警器",
                        "eventTime", "2024-12-24",
                        "deviceKey", "123344",
                        "eventTypeName", "告警",
                        "eventContent", Map.of("液位", 100,
                                "类型", "火警")));
        return R.success(true);
    }
}
