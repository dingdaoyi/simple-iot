package com.github.dingdaoyi.config.base;

import com.github.dingdaoyi.entity.SmsConfig;
import com.github.dingdaoyi.model.enu.SmsSupplier;
import com.github.dingdaoyi.service.SmsConfigService;
import jakarta.annotation.Resource;
import org.dromara.sms4j.core.datainterface.SmsReadConfig;
import org.dromara.sms4j.provider.config.BaseConfig;
import org.dromara.sms4j.tencent.config.TencentConfig;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author dingyunwei
 */
@Component
public class ReadConfig implements SmsReadConfig {
    @Resource
    private SmsConfigService smsConfigService;
    @Override
    public BaseConfig getSupplierConfig(String configId) {
        Optional<SmsConfig> defaultConfig = smsConfigService.getDefaultConfig();
        if (defaultConfig.isEmpty()) {
            return null;
        }
        SmsConfig smsConfig = defaultConfig.get();
        return switch (smsConfig.getSupplier()) {
            case SmsSupplier.TENCENT -> parseTencentConfig(smsConfig);
            default -> null;
        };

    }

    private BaseConfig parseTencentConfig(SmsConfig smsConfig) {
        TencentConfig tencentConfig = new TencentConfig();
        tencentConfig.setAccessKeyId(smsConfig.getAccessKey());
        tencentConfig.setSignature(smsConfig.getSignName());
        tencentConfig.setAccessKeySecret(smsConfig.getSecretKey());
        Map<String, String> configJson = smsConfig.getConfigJson();
        tencentConfig.setSdkAppId(configJson.get("sdkAppId"));
        return tencentConfig;
    }

    @Override
    public List<BaseConfig> getSupplierConfigList() {
        return null;
    }
}
