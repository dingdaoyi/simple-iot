package com.github.dingdaoyi.service.impl;

import com.github.dingdaoyi.service.NotificationService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author dingyunwei
 */
@Service
@Slf4j
public class EmailNotificationService implements NotificationService {

    @Resource
    private JavaMailSender mailSender;

    @PostConstruct
    void init() {
        log.info("init email notification service");
        log.info("host:{}", ((JavaMailSenderImpl) mailSender).getHost());
    }

    @Override
    public void sendMessage(String receiver, String templateId, Map<String, Object> model) {

    }
}
