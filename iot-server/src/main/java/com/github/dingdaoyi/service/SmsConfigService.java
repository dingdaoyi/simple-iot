package com.github.dingdaoyi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.dingdaoyi.entity.SmsConfig;
import com.github.dingdaoyi.model.query.SmsSendWithTemplateQuery;

import java.util.Optional;

/**
 * 短信配置服务接口
 * @author dingyunwei
 */
public interface SmsConfigService extends IService<SmsConfig> {
    
    /**
     * 获取默认短信配置
     */
    Optional<SmsConfig> getDefaultConfig();
    
    /**
     * 设置默认配置
     */
    Boolean setDefault(Integer id);
    
    /**
     * 发送短信
     */
    Boolean sendSms(String phone, String message);

    /**
     * 使用模板发送短信
     */
    Boolean sendSmsWithTemplate(SmsSendWithTemplateQuery query);

}