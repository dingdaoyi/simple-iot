package com.github.dingdaoyi.controller.iot;

import com.github.dingdaoyi.controller.iot.dto.PushTestRequest;
import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.service.PushConfigService;
import com.github.dingdaoyi.service.push.PushDeliveryResult;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PushConfigControllerTest {

    private final PushConfigService pushConfigService = mock(PushConfigService.class);
    private final PushConfigController controller = new PushConfigController(pushConfigService);

    @Test
    void testPushDelegatesToServiceAndReturnsDeliveryResult() {
        PushTestRequest request = new PushTestRequest();
        request.setPayload("{\"temperature\":36.5}");
        request.setEventType("push.test");
        PushDeliveryResult result = PushDeliveryResult.success(200, "HTTP推送成功", "ok");
        when(pushConfigService.testPush(10, request)).thenReturn(result);

        BaseResult<PushDeliveryResult> response = controller.testPush(10, request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isSameAs(result);
        verify(pushConfigService).testPush(10, request);
    }
}
