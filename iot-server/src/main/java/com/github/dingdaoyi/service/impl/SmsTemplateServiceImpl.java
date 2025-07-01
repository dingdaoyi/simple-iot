package com.github.dingdaoyi.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dingdaoyi.entity.SmsTemplate;
import com.github.dingdaoyi.mapper.SmsTemplateMapper;
import com.github.dingdaoyi.model.enu.SmsTemplateType;
import com.github.dingdaoyi.service.SmsTemplateService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 短信模板服务实现
 * @author dingyunwei
 */
@Service
public class SmsTemplateServiceImpl extends ServiceImpl<SmsTemplateMapper, SmsTemplate> implements SmsTemplateService {
    
    @Override
    public Optional<SmsTemplate> getByConfigAndType(Integer configId, SmsTemplateType templateType) {
        SmsTemplate template = getOne(Wrappers.<SmsTemplate>lambdaQuery()
                .eq(SmsTemplate::getConfigId, configId)
                .eq(SmsTemplate::getTemplateType, templateType)
                .eq(SmsTemplate::getStatus, 1));
        return Optional.ofNullable(template);
    }
    
    @Override
    public List<SmsTemplate> listByConfigId(Integer configId) {
        return list(Wrappers.<SmsTemplate>lambdaQuery()
                .eq(SmsTemplate::getConfigId, configId)
                .orderByDesc(SmsTemplate::getCreateTime));
    }
}