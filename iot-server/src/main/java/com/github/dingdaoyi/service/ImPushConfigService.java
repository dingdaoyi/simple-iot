package com.github.dingdaoyi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.dingdaoyi.entity.ImPushConfig;
import com.github.dingdaoyi.model.enu.NotifyType;

import java.util.List;
import java.util.Optional;

public interface ImPushConfigService extends IService<ImPushConfig> {

    Optional<ImPushConfig> getEnabledByPlatform(NotifyType platform);

    List<ImPushConfig> listEnabled();
}
