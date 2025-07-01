package com.github.dingdaoyi.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dingdaoyi.entity.SmsConfig;
import com.github.dingdaoyi.entity.SmsTemplate;
import com.github.dingdaoyi.mapper.SmsConfigMapper;
import com.github.dingdaoyi.model.enu.SmsTemplateType;
import com.github.dingdaoyi.model.query.SmsSendWithTemplateQuery;
import com.github.dingdaoyi.service.SmsConfigService;
import com.github.dingdaoyi.service.SmsTemplateService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.sms4j.api.entity.SmsResponse;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Optional;

/**
 * 短信配置服务实现
 * @author dingyunwei
 */
@Service
@Slf4j
public class SmsConfigServiceImpl extends ServiceImpl<SmsConfigMapper, SmsConfig> implements SmsConfigService {
    
    @Resource
    private SmsTemplateService smsTemplateService;


    @Override
    public Optional<SmsConfig> getDefaultConfig() {
        SmsConfig config = getOne(Wrappers.<SmsConfig>lambdaQuery()
                .eq(SmsConfig::getIsDefault, true)
                .eq(SmsConfig::getStatus, 1));
        return Optional.ofNullable(config);
    }
    
    @Override
    @Transactional
    public Boolean setDefault(Integer id) {
        // 清除所有默认配置
        update(Wrappers.<SmsConfig>lambdaUpdate()
                .set(SmsConfig::getIsDefault, false));
        
        // 设置新的默认配置
        return update(Wrappers.<SmsConfig>lambdaUpdate()
                .set(SmsConfig::getIsDefault, true)
                .eq(SmsConfig::getId, id));
    }
    
    @Override
    public Boolean sendSms(String phone, String message) {
        Optional<SmsConfig> configOpt = getDefaultConfig();
        if (configOpt.isEmpty()) {
            log.error("未找到默认短信配置");
            return false;
        }
        return sendSmsWithConfig(configOpt.get(), phone, message);
    }

    
    @Override
    public Boolean sendSmsWithTemplate(SmsSendWithTemplateQuery query) {
        try {
            SmsConfig config;
            if (query.getConfigId() != null) {
                config = getById(query.getConfigId());
                if (config == null || config.getStatus() != 1) {
                    log.error("短信配置不存在或已禁用: {}", query.getConfigId());
                    return false;
                }
            } else {
                Optional<SmsConfig> configOpt = getDefaultConfig();
                if (configOpt.isEmpty()) {
                    log.error("未找到默认短信配置");
                    return false;
                }
                config = configOpt.get();
            }
            
            Optional<SmsTemplate> templateOpt = smsTemplateService.getByConfigAndType(config.getId(), query.getTemplateType());
            if (templateOpt.isEmpty()) {
                log.error("未找到对应的短信模板: configId={}, templateType={}", config.getId(), query.getTemplateType());
                return false;
            }
            
            SmsTemplate template = templateOpt.get();
            return sendSmsWithTemplate(config, template, query.getPhone(), query.getTemplateParams());
        } catch (Exception e) {
            log.error("使用模板发送短信失败", e);
            return false;
        }
    }

    private Boolean sendSmsWithConfig(SmsConfig config, String phone, String message) {
        try {
            log.info("发送短信: 供应商={}, 手机号={}, 消息={}", config.getSupplier(), phone, message);
            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            map.put("code", message);
            Optional<SmsTemplate> templateOpt = smsTemplateService.getByConfigAndType(config.getId(), SmsTemplateType.VERIFY_CODE);
            if (templateOpt.isEmpty()) {
                log.error("未找到对应的短信模板: configId={}", config.getId());
                return false;
            }
            SmsResponse smsResponse = SmsFactory.getSmsBlend(config.getSupplier().getCode())
                    .sendMessage(phone, templateOpt.get().getTemplateId(), map);
            log.info("结果:{}", JSONUtil.toJsonStr(smsResponse));
            return true;
        } catch (Exception e) {
            log.error("发送短信失败", e);
            return false;
        }
    }
    
    private Boolean sendSmsWithTemplate(SmsConfig config, SmsTemplate template, String phone, java.util.Map<String, Object> params) {
        try {
            log.info("使用模板发送短信: 供应商={}, 手机号={}, 模板={}", config.getSupplier(), phone, template.getTemplateId());
            LinkedHashMap<String, String> templateParams = new LinkedHashMap<>();
            if (params != null) {
                params.forEach((k, v) -> templateParams.put(k, String.valueOf(v)));
            }
            SmsResponse smsResponse = SmsFactory.getSmsBlend(config.getSupplier().getCode())
                    .sendMessage(phone, template.getTemplateId(), templateParams);
            log.info("结果:{}", JSONUtil.toJsonStr(smsResponse));
            return smsResponse.isSuccess();
        } catch (Exception e) {
            log.error("使用模板发送短信失败", e);
            return false;
        }
    }
}