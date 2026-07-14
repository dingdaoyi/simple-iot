package com.github.dingdaoyi.controller.iot;

import com.github.dingdaoyi.controller.iot.dto.RuleChainDebugRequest;
import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.entity.RuleChain;
import com.github.dingdaoyi.model.vo.RuleChainDebugResultVo;
import com.github.dingdaoyi.model.vo.RuleChainValidationResultVo;
import com.github.dingdaoyi.rule.RuleContext;
import com.github.dingdaoyi.service.ProductService;
import com.github.dingdaoyi.service.RuleChainService;
import com.github.dingdaoyi.service.TslModelService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RuleChainControllerDebugTest {

    private final RuleChainService ruleChainService = mock(RuleChainService.class);
    private final ProductService productService = mock(ProductService.class);
    private final TslModelService tslModelService = mock(TslModelService.class);
    private final com.github.dingdaoyi.service.RuleExecutionLogService ruleExecutionLogService = mock(com.github.dingdaoyi.service.RuleExecutionLogService.class);
    private final com.github.dingdaoyi.rule.RuleChainEngine ruleChainEngine = mock(com.github.dingdaoyi.rule.RuleChainEngine.class);
    private final RuleChainController controller = new RuleChainController(ruleChainService, productService, tslModelService, ruleExecutionLogService, ruleChainEngine);

    @Test
    void debugDelegatesDraftRuleChainToService() {
        RuleChainDebugRequest request = new RuleChainDebugRequest();
        RuleChain ruleChain = new RuleChain();
        ruleChain.setName("草稿规则链");
        request.setRuleChain(ruleChain);
        request.setMessageType(RuleContext.MessageType.PROPERTY);
        request.setProperties(Map.of("temperature", 36.5));

        RuleChainDebugResultVo result = new RuleChainDebugResultVo();
        result.setRuleChainName("草稿规则链");
        result.setSuccess(true);
        result.setExecutedNodeCount(1);
        result.setTraces(List.of());

        when(ruleChainService.debug(request)).thenReturn(result);

        BaseResult<RuleChainDebugResultVo> response = controller.debug(request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isSameAs(result);
        verify(ruleChainService).debug(request);
    }

    @Test
    void validateDelegatesDraftRuleChainToService() {
        RuleChain ruleChain = new RuleChain();
        ruleChain.setName("草稿规则链");
        RuleChainValidationResultVo result = new RuleChainValidationResultVo();
        result.setValid(true);

        when(ruleChainService.validateDraft(ruleChain)).thenReturn(result);

        BaseResult<RuleChainValidationResultVo> response = controller.validate(ruleChain);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isSameAs(result);
        verify(ruleChainService).validateDraft(ruleChain);
    }
}
