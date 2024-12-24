package com.github.dingdaoyi.service;

import java.util.Map;

public interface NotificationService {

    /**
     * 通知
     *
     * @param receiver 接收人
     * @param templateId  模板id
     * @param model 参数
     */
    void sendMessage(String receiver, String templateId, Map<String, Object> model);
}
