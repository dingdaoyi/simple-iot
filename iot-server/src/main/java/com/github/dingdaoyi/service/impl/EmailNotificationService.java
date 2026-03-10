package com.github.dingdaoyi.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.dingdaoyi.entity.EmailConfig;
import com.github.dingdaoyi.model.enu.NotifyType;
import com.github.dingdaoyi.core.enums.ResultCode;
import com.github.dingdaoyi.core.exception.BusinessException;
import com.github.dingdaoyi.service.EmailConfigService;
import com.github.dingdaoyi.service.NotificationService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * 邮件通知服务 - 从数据库读取配置
 * @author dingyunwei
 */
@Service
@Slf4j
public class EmailNotificationService implements NotificationService {

    @Resource
    private EmailConfigService emailConfigService;

    @Override
    public NotifyType getNotifyType() {
        return NotifyType.EMAIL;
    }

    @Override
    public void sendMessage(String receiver, String templateId, Map<String, Object> model) {
        // 从数据库获取默认配置
        Optional<EmailConfig> configOpt = emailConfigService.getDefaultConfig();
        if (configOpt.isEmpty()) {
            log.warn("未配置邮箱服务，跳过邮件发送");
            return;
        }

        EmailConfig config = configOpt.get();
        if (!config.isEnabled()) {
            log.warn("邮箱配置已禁用，跳过邮件发送");
            return;
        }

        try {
            log.info("发送邮件:{}:{}:{}", receiver, templateId, JSONUtil.toJsonStr(model));

            // 动态创建邮件发送器
            JavaMailSender mailSender = createMailSender(config);

            String emailBody = generateEmailBody(templateId, model);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, config.getEncoding());
            helper.setFrom(config.getUsername());
            helper.setTo(receiver);
            helper.setSubject("设备事件通知");
            helper.setText(emailBody, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("发送消息错误:{}", e.getMessage());
        }
    }

    /**
     * 动态创建邮件发送器
     */
    private JavaMailSender createMailSender(EmailConfig config) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(config.getHost());
        sender.setPort(config.getPort());
        sender.setUsername(config.getUsername());
        sender.setPassword(config.getPassword());
        sender.setProtocol(config.getProtocol() != null ? config.getProtocol() : "smtp");
        sender.setDefaultEncoding(config.getEncoding() != null ? config.getEncoding() : "UTF-8");

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", config.getSslEnabled() != null ? config.getSslEnabled() : true);
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");
        sender.setJavaMailProperties(props);

        return sender;
    }

    private String generateEmailBody(String templateName, Map<String, Object> templateData) {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_33);
        cfg.setClassLoaderForTemplateLoading(this.getClass().getClassLoader(), "templates");
        try {
            Template template = cfg.getTemplate(templateName);
            StringWriter writer = new StringWriter();
            template.process(templateData, writer);
            return writer.toString();
        } catch (IOException | TemplateException e) {
            throw new BusinessException(ResultCode.NPE_ERROR, "生成邮件内容失败");
        }
    }
}
