package com.github.dingdaoyi.controller.system;

import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.core.enums.ResultCode;
import com.github.dingdaoyi.core.exception.BusinessException;
import com.github.dingdaoyi.entity.EmailConfig;
import com.github.dingdaoyi.service.EmailConfigService;
import com.github.dingdaoyi.service.impl.EmailNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * 消息通知控制器
 * @author dingyunwei
 */
@RestController
@Tag(name = "消息通知")
@RequestMapping("/notification")
public class NotificationController {

    @Resource
    private EmailNotificationService notificationService;
    @Resource
    private EmailConfigService emailConfigService;

    @Operation(summary = "发送测试消息（使用默认配置）")
    @GetMapping
    public BaseResult<Boolean> sendTest() {
        notificationService.sendMessage("1493001032@qq.com", "event_notification",
                Map.of("deviceName", "烟雾报警器",
                        "eventTime", "2024-12-24",
                        "deviceKey", "123344",
                        "eventTypeName", "告警",
                        "eventContent", Map.of("液位", 100,
                                "类型", "火警")));
        return BaseResult.success(true);
    }

    @Operation(summary = "发送测试邮件")
    @PostMapping("/email/test")
    public BaseResult<Boolean> sendTestEmail(@RequestBody TestEmailRequest request) {
        // 获取配置
        EmailConfig config;
        if (request.getConfigId() != null) {
            config = emailConfigService.getById(request.getConfigId());
            if (config == null) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "邮箱配置不存在");
            }
        } else {
            Optional<EmailConfig> configOpt = emailConfigService.getDefaultConfig();
            if (configOpt.isEmpty()) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "未配置默认邮箱服务");
            }
            config = configOpt.get();
        }

        if (!config.isEnabled()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "邮箱配置已禁用");
        }

        try {
            // 动态创建邮件发送器
            JavaMailSender mailSender = createMailSender(config);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, config.getEncoding());
            helper.setFrom(config.getUsername());
            helper.setTo(request.getTo());
            helper.setSubject("IoT平台测试邮件");
            helper.setText("<h2>这是一封测试邮件</h2><p>如果您收到此邮件，说明邮箱配置成功！</p><p>发送时间：" + LocalDateTime.now() + "</p>", true);
            mailSender.send(message);
            return BaseResult.success(true);
        } catch (MessagingException e) {
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "邮件发送失败: " + e.getMessage());
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

    /**
     * 测试邮件请求
     */
    @Data
    public static class TestEmailRequest {
        @NotBlank(message = "收件人邮箱不能为空")
        @Email(message = "邮箱格式不正确")
        private String to;
        private Integer configId;
    }
}
