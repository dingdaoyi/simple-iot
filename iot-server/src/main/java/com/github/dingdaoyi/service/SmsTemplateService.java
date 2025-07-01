package com.github.dingdaoyi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.dingdaoyi.entity.SmsTemplate;
import com.github.dingdaoyi.model.enu.SmsTemplateType;

import java.util.List;
import java.util.Optional;

/**
 * 短信模板服务接口
 * @author dingyunwei
 */
public interface SmsTemplateService extends IService<SmsTemplate> {
    
    /**
     * 根据配置ID和模板类型获取模板
     */
    Optional<SmsTemplate> getByConfigAndType(Integer configId, SmsTemplateType templateType);
    
    /**
     * 根据配置ID获取模板列表
     */
    List<SmsTemplate> listByConfigId(Integer configId);
}