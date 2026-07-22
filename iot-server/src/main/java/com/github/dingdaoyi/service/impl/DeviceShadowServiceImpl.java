package com.github.dingdaoyi.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dingdaoyi.entity.DeviceShadow;
import com.github.dingdaoyi.mapper.DeviceShadowMapper;
import com.github.dingdaoyi.service.DeviceShadowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author dingyunwei
 */
@Slf4j
@Service
public class DeviceShadowServiceImpl extends ServiceImpl<DeviceShadowMapper, DeviceShadow> implements DeviceShadowService {

    @Override
    public Optional<DeviceShadow> getByDeviceId(Integer deviceId) {
        return lambdaQuery().eq(DeviceShadow::getDeviceId, deviceId).oneOpt();
    }

    @Override
    public DeviceShadow updateDesired(Integer deviceId, JSONObject desired) {
        DeviceShadow shadow = getOrCreate(deviceId);
        JSONObject existing = parseJson(shadow.getDesiredState());
        existing.putAll(desired);
        shadow.setDesiredState(existing.toString());
        shadow.setVersion(shadow.getVersion() + 1);
        updateById(shadow);
        log.info("设备影子 desired 更新;deviceId={}|version={}", deviceId, shadow.getVersion());
        return shadow;
    }

    @Override
    public DeviceShadow updateReported(Integer deviceId, JSONObject reported) {
        DeviceShadow shadow = getOrCreate(deviceId);
        JSONObject existing = parseJson(shadow.getReportedState());
        existing.putAll(reported);
        shadow.setReportedState(existing.toString());
        shadow.setVersion(shadow.getVersion() + 1);
        updateById(shadow);
        return shadow;
    }

    @Override
    public void clearDesired(Integer deviceId) {
        DeviceShadow shadow = getByDeviceId(deviceId).orElse(null);
        if (shadow != null) {
            shadow.setDesiredState("{}");
            shadow.setVersion(shadow.getVersion() + 1);
            updateById(shadow);
        }
    }

    private DeviceShadow getOrCreate(Integer deviceId) {
        return getByDeviceId(deviceId).orElseGet(() -> {
            DeviceShadow s = new DeviceShadow();
            s.setDeviceId(deviceId);
            s.setDesiredState("{}");
            s.setReportedState("{}");
            s.setVersion(0);
            save(s);
            return s;
        });
    }

    private JSONObject parseJson(String json) {
        if (StrUtil.isBlank(json)) {
            return new JSONObject();
        }
        try {
            return JSONUtil.parseObj(json);
        } catch (Exception e) {
            log.warn("解析影子 JSON 失败，重置为空: {}", json);
            return new JSONObject();
        }
    }
}
