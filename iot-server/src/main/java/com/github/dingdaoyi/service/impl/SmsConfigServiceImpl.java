package com.github.dingdaoyi.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dingdaoyi.entity.SmsConfig;
import com.github.dingdaoyi.mapper.SmsConfigMapper;
import com.github.dingdaoyi.service.SmsConfigService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.sms4j.api.entity.SmsResponse;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private Boolean sendSmsWithConfig(SmsConfig config, String phone, String message) {
        try {
            log.info("发送短信: 供应商={}, 手机号={}, 消息={}", config.getSupplier(), phone, message);
            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            map.put("code", message);
            SmsResponse smsResponse = SmsFactory.getSmsBlend(config.getSupplier())
                    .sendMessage(phone, "1408092", map);
            log.info("结果:{}", JSONUtil.toJsonStr(smsResponse));
            return true;
        } catch (Exception e) {
            log.error("发送短信失败", e);
            return false;
        }
    }
    
    private Boolean sendAlibabaSms(SmsConfig config, String phone, String message) {
        log.info("使用阿里云发送短信: phone={}, message={}", phone, message);
        // TODO: 集成阿里云SMS SDK
        return true;
    }
    
    private Boolean sendTencentSms(SmsConfig config, String phone, String message) {
        log.info("使用腾讯云发送短信: phone={}, message={}", phone, message);
        // TODO: 集成腾讯云SMS SDK
        return true;
    }
    
    private Boolean sendHuaweiSms(SmsConfig config, String phone, String message) {
        log.info("使用华为云发送短信: phone={}, message={}", phone, message);
        // TODO: 集成华为云SMS SDK
        return true;
    }
}