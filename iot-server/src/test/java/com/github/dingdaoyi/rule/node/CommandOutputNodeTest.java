package com.github.dingdaoyi.rule.node;

import com.github.dingdaoyi.entity.enu.RuleNodeType;
import com.github.dingdaoyi.model.vo.DeviceVo;
import com.github.dingdaoyi.rule.RuleContext;
import com.github.dingdaoyi.rule.config.OutputCommandConfig;
import com.github.dingdaoyi.service.DeviceService;
import com.github.dingdaoyi.service.ServiceHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandOutputNodeTest {

    @Mock
    private DeviceService deviceService;
    @Mock
    private ServiceHandler serviceHandler;

    private CommandOutputNode node;

    @BeforeEach
    void setUp() throws Exception {
        node = new CommandOutputNode();
        for (var field : CommandOutputNode.class.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getType() == DeviceService.class) field.set(node, deviceService);
            if (field.getType() == ServiceHandler.class) field.set(node, serviceHandler);
        }
    }

    private RuleContext buildContext() {
        RuleContext ctx = new RuleContext();
        ctx.setDeviceKey("dev-1");
        ctx.setDeviceName("测试设备");
        ctx.setDeviceId(1);
        return ctx;
    }

    @Test
    void getNodeTypeReturnsOutputCommand() {
        assertThat(node.getNodeType()).isEqualTo(RuleNodeType.OUTPUT_COMMAND);
    }

    @Test
    void failsWhenIdentifierIsNull() {
        OutputCommandConfig config = new OutputCommandConfig(); // identifier is null

        var result = node.execute(buildContext(), config);

        assertThat(result.success()).isFalse();
        assertThat(result.message()).contains("指令标识符不能为空");
    }

    @Test
    void sendsCommandToSelfDevice() throws Exception {
        when(serviceHandler.sendMessage(eq("dev-1"), eq("restart"), anyMap()))
                .thenReturn(Map.of("result", "ok"));

        OutputCommandConfig config = new OutputCommandConfig();
        config.setIdentifier("restart");
        config.setTargetMode("self");

        var result = node.execute(buildContext(), config);

        assertThat(result.success()).isTrue();
        assertThat(result.message()).contains("测试设备").contains("restart");
    }

    @Test
    void sendsCommandToOtherDevice() throws Exception {
        DeviceVo target = new DeviceVo();
        target.setDeviceKey("dev-2");
        target.setDeviceName("目标设备");
        when(deviceService.details(2)).thenReturn(Optional.of(target));
        when(serviceHandler.sendMessage(eq("dev-2"), eq("reboot"), anyMap()))
                .thenReturn(Map.of());

        OutputCommandConfig config = new OutputCommandConfig();
        config.setIdentifier("reboot");
        config.setTargetMode("other");
        config.setTargetDeviceId(2);

        var result = node.execute(buildContext(), config);

        assertThat(result.success()).isTrue();
        assertThat(result.message()).contains("目标设备").contains("reboot");
        verify(deviceService).details(2);
    }

    @Test
    void failsWhenOtherDeviceNotFound() {
        when(deviceService.details(99)).thenReturn(Optional.empty());

        OutputCommandConfig config = new OutputCommandConfig();
        config.setIdentifier("reboot");
        config.setTargetMode("other");
        config.setTargetDeviceId(99);

        var result = node.execute(buildContext(), config);

        assertThat(result.success()).isFalse();
        assertThat(result.message()).contains("目标设备不存在");
    }

    @Test
    void failsWhenOtherDeviceIdIsNull() {
        OutputCommandConfig config = new OutputCommandConfig();
        config.setIdentifier("reboot");
        config.setTargetMode("other"); // targetDeviceId is null

        var result = node.execute(buildContext(), config);

        assertThat(result.success()).isFalse();
        assertThat(result.message()).contains("目标设备ID不能为空");
    }

    @Test
    void returnsFailureOnHandlerException() throws Exception {
        when(serviceHandler.sendMessage(eq("dev-1"), eq("restart"), anyMap()))
                .thenThrow(new RuntimeException("设备离线"));

        OutputCommandConfig config = new OutputCommandConfig();
        config.setIdentifier("restart");

        var result = node.execute(buildContext(), config);

        assertThat(result.success()).isFalse();
        assertThat(result.message()).contains("设备离线");
    }
}
