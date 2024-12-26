package com.github.dingdaoyi.service;

import com.github.dingdaoyi.model.enu.NotifyType;

import java.util.Map;

/**
 * @author dingyunwei
 */
public interface NotificationService {

    /**
     * 消息类型
     * @return
     */
    NotifyType getNotifyType();
    /**
     * 通知
     *
     * @param receiver 接收人
     * @param templateId  模板id
     * @param model 参数
     */
    void sendMessage(String receiver, String templateId, Map<String, Object> model);
}
