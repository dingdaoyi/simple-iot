package com.github.dingdaoyi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dingdaoyi.controller.iot.dto.PushTestRequest;
import com.github.dingdaoyi.core.base.PageResult;
import com.github.dingdaoyi.core.enums.ResultCode;
import com.github.dingdaoyi.core.exception.BusinessException;
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
    public boolean save(PushConfig entity) {
        validateAndNormalize(entity);
        return super.save(entity);
    }

    @Override
    public boolean updateById(PushConfig entity) {
        validateAndNormalize(entity);
        return super.updateById(entity);
    }

    private void validateAndNormalize(PushConfig config) {
        if (config.getType() == PushConfigType.HTTP) {
            validateHttpConfig(config);
            clearMqttConfig(config);
            return;
        }
        if (config.getType() == PushConfigType.MQTT) {
            validateMqttConfig(config);
            clearHttpConfig(config);
            return;
        }
        throw new BusinessException(ResultCode.BAD_REQUEST, "配置类型不支持");
    }

    private void validateHttpConfig(PushConfig config) {
        String httpUrl = StrUtil.trim(config.getHttpUrl());
        if (StrUtil.isBlank(httpUrl)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "HTTP请求URL不能为空");
        }
        if (!StrUtil.startWithIgnoreCase(httpUrl, "http://") && !StrUtil.startWithIgnoreCase(httpUrl, "https://")) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "HTTP请求URL必须以 http:// 或 https:// 开头");
        }
        config.setHttpUrl(httpUrl);
        config.setHttpMethod(StrUtil.blankToDefault(StrUtil.trim(config.getHttpMethod()), "POST").toUpperCase());
        if (config.getHttpTimeout() == null) {
            config.setHttpTimeout(5000);
        }
    }

    private void validateMqttConfig(PushConfig config) {
        String broker = StrUtil.trim(config.getMqttBroker());
        String topic = StrUtil.trim(config.getMqttTopic());
        if (StrUtil.isBlank(broker)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "MQTT Broker地址不能为空");
        }
        if (!StrUtil.startWithAnyIgnoreCase(broker, "tcp://", "ssl://", "ws://", "wss://")) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "MQTT Broker地址必须以 tcp://、ssl://、ws:// 或 wss:// 开头");
        }
        if (StrUtil.isBlank(topic)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "MQTT目标Topic不能为空");
        }
        if (StrUtil.containsAny(topic, '#', '+')) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "MQTT发布Topic不能包含通配符 # 或 +");
        }
        config.setMqttBroker(broker);
        config.setMqttTopic(topic);
        config.setMqttUsername(StrUtil.trim(config.getMqttUsername()));
        config.setMqttClientId(StrUtil.trim(config.getMqttClientId()));
        if (config.getMqttQos() == null) {
            config.setMqttQos(0);
        }
        if (config.getMqttKeepAlive() == null) {
            config.setMqttKeepAlive(60);
        }
        if (config.getMqttRetain() == null) {
            config.setMqttRetain(false);
        }
        if (config.getMqttCleanSession() == null) {
            config.setMqttCleanSession(true);
        }
    }

    private void clearHttpConfig(PushConfig config) {
        config.setHttpUrl(null);
        config.setHttpMethod("POST");
        config.setHttpHeaders(List.of());
        config.setHttpTimeout(5000);
    }

    private void clearMqttConfig(PushConfig config) {
        config.setMqttBroker(null);
        config.setMqttUsername(null);
        config.setMqttPassword(null);
        config.setMqttClientId(null);
        config.setMqttTopic(null);
        config.setMqttQos(0);
        config.setMqttRetain(false);
        config.setMqttKeepAlive(60);
        config.setMqttCleanSession(true);
        config.setMqttOptions(List.of());
    }

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
        // 测试推送仅用于联通性验证，不强制要求配置已启用。
        if (config.getType() != PushConfigType.HTTP) {
            return PushDeliveryResult.failure("当前仅支持 HTTP 推送配置测试");
        }
        if (config.getHttpUrl() == null || config.getHttpUrl().isBlank()) {
            return PushDeliveryResult.failure("HTTP URL 未配置，无法发起测试推送");
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
