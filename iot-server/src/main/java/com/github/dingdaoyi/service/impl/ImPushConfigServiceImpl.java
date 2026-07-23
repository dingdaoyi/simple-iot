package com.github.dingdaoyi.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dingdaoyi.entity.ImPushConfig;
import com.github.dingdaoyi.mapper.ImPushConfigMapper;
import com.github.dingdaoyi.model.enu.NotifyType;
import com.github.dingdaoyi.service.ImPushConfigService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ImPushConfigServiceImpl extends ServiceImpl<ImPushConfigMapper, ImPushConfig>
    implements ImPushConfigService {

    @Override
    public Optional<ImPushConfig> getEnabledByPlatform(NotifyType platform) {
        return lambdaQuery()
            .eq(ImPushConfig::getPlatform, platform)
            .eq(ImPushConfig::getIsEnabled, true)
            .last("LIMIT 1")
            .oneOpt();
    }

    @Override
    public List<ImPushConfig> listEnabled() {
        return lambdaQuery()
            .eq(ImPushConfig::getIsEnabled, true)
            .list();
    }
}
