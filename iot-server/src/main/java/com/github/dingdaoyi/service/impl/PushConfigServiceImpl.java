package com.github.dingdaoyi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dingdaoyi.controller.iot.dto.PushTestRequest;
import com.github.dingdaoyi.core.base.PageResult;
import com.github.dingdaoyi.entity.PushConfig;
import com.github.dingdaoyi.entity.enu.PushConfigType;
import com.github.dingdaoyi.mapper.PushConfigMapper;
import com.github.dingdaoyi.model.query.PushConfigPageQuery;
import com.github.dingdaoyi.model.vo.PushConfigDetailVo;
import com.github.dingdaoyi.model.vo.PushConfigPageVo;
import com.github.dingdaoyi.service.PushConfigService;
import com.github.dingdaoyi.service.push.HttpPushDeliveryService;
import com.github.dingdaoyi.service.push.PushDeliveryResult;
import com.github.dingdaoyi.utils.PageHelper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 推送配置服务实现
 * @author dingyunwei
 */
@Service
public class PushConfigServiceImpl extends ServiceImpl<PushConfigMapper, PushConfig> implements PushConfigService {

    @Resource
    private HttpPushDeliveryService httpPushDeliveryService;

    @Override
    public PageResult<PushConfigPageVo> pageByQuery(PushConfigPageQuery query) {
        Page<PushConfigPageVo> page = PageHelper.page(query);
        Page<PushConfigPageVo> result = baseMapper.pageByQuery(page, query);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public Optional<PushConfigDetailVo> details(Integer id) {
        PushConfig config = baseMapper.selectById(id);
        if (config == null) {
            return Optional.empty();
        }
        PushConfigDetailVo vo = new PushConfigDetailVo();
        BeanUtil.copyProperties(config, vo);
        return Optional.of(vo);
    }

    @Override
    public List<PushConfig> listEnabled() {
        return baseMapper.listEnabled();
    }

    @Override
    public PushDeliveryResult testPush(Integer id, PushTestRequest request) {
        PushConfig config = getById(id);
        if (config == null) {
            return PushDeliveryResult.failure("推送配置不存在: " + id);
        }
        if (!Boolean.TRUE.equals(config.getIsEnabled())) {
            return PushDeliveryResult.failure("推送配置已禁用: " + config.getName());
        }
        if (config.getType() != PushConfigType.HTTP) {
            return PushDeliveryResult.failure("当前仅支持 HTTP 推送配置测试");
        }
        Object payload = request != null && request.getPayload() != null ? request.getPayload() : defaultTestPayload();
        String eventType = request != null && request.getEventType() != null ? request.getEventType() : "push.test";
        return httpPushDeliveryService.deliverHttp(config, payload, eventType);
    }

    private Object defaultTestPayload() {
        return java.util.Map.of(
            "event", "push.test",
            "deviceKey", "demo-device",
            "properties", java.util.Map.of("temperature", 36.5)
        );
    }
}
