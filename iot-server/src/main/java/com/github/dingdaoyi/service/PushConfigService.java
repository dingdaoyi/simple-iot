package com.github.dingdaoyi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.dingdaoyi.controller.iot.dto.PushTestRequest;
import com.github.dingdaoyi.core.base.PageResult;
import com.github.dingdaoyi.entity.PushConfig;
import com.github.dingdaoyi.model.query.PushConfigPageQuery;
import com.github.dingdaoyi.model.vo.PushConfigDetailVo;
import com.github.dingdaoyi.model.vo.PushConfigPageVo;
import com.github.dingdaoyi.service.push.PushDeliveryResult;

import java.util.List;
import java.util.Optional;

/**
 * 推送配置服务接口
 * @author dingyunwei
 */
public interface PushConfigService extends IService<PushConfig> {

    /**
     * 分页查询
     */
    PageResult<PushConfigPageVo> pageByQuery(PushConfigPageQuery query);

    /**
     * 获取详情
     */
    Optional<PushConfigDetailVo> details(Integer id);

    /**
     * 获取所有启用的配置
     */
    List<PushConfig> listEnabled();

    /**
     * 使用指定配置发送一次测试推送。
     */
    PushDeliveryResult testPush(Integer id, PushTestRequest request);
}
