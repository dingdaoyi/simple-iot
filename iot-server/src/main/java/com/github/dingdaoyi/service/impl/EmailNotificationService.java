package com.github.dingdaoyi.service.impl;

import com.github.dingdaoyi.model.enu.NotifyType;
import com.github.dingdaoyi.model.enu.SysCodeEnum;
import com.github.dingdaoyi.service.NotificationService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.core.exception.ServiceException;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * @author dingyunwei
 */
@Service
@Slf4j
public class EmailNotificationService implements NotificationService {

    @Resource
    private JavaMailSender mailSender;
    @Resource
    private MailProperties mailProperties;
    @PostConstruct
    void init() {
        log.info("init email notification service");
        log.info("host:{}", ((JavaMailSenderImpl) mailSender).getHost());
    }

    @Override
    public NotifyType getNotifyType() {
        return NotifyType.EMAIL;
    }

    @Override
    public void sendMessage(String receiver, String templateId, Map<String, Object> model) {
        try {
            String emailBody = generateEmailBody(templateId+".ftl",model);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mailProperties.getUsername());
            helper.setTo(receiver);
            helper.setSubject("设备事件通知");
            helper.setText(emailBody, true);
            mailSender.send(message);
        } catch (MessagingException e) {
           log.error("发送消息错误:{}", e.getMessage());
        }
    }


    private  String generateEmailBody(String templateName,Map<String, Object> templateData) {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_33);
        cfg.setClassLoaderForTemplateLoading(this.getClass().getClassLoader(), "templates");
        try {
            Template template = cfg.getTemplate(templateName);
            StringWriter writer = new StringWriter();
            template.process(templateData, writer);
            return writer.toString();
        } catch (IOException | TemplateException e) {
            throw new ServiceException(SysCodeEnum.NPE_ERROR, "生成邮件内容失败");
        }
    }
}
