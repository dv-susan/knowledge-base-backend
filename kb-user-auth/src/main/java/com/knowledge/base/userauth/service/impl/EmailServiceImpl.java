package com.knowledge.base.userauth.service.impl;

import com.knowledge.base.userauth.service.EmailService;
import com.knowledge.base.userauth.service.SecurityConfigService;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * 邮件发送服务实现
 *
 * <p>负责发送 HTML 格式的账户激活邮件和密码重置验证码邮件，
 * 邮件中的系统名称从 {@code system.name} 配置动态获取。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Resource
    private JavaMailSender mailSender;

    @Resource
    private SecurityConfigService securityConfigService;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${app.frontend-url:http://localhost:3002}")
    private String frontendUrl;

    /**
     * 获取系统名称（从安全配置缓存读取，默认"知识库系统"）
     */
    private String getSystemName() {
        String value = securityConfigService.getConfig("system.name");
        if (value != null && !value.isBlank()) {
            return value.trim();
        }
        return "知识库系统";
    }

    @Override
    public void sendActivationEmail(String to, String username, String token) {
        String activationUrl = frontendUrl + "/activate?token=" + token;
        String systemName = getSystemName();

        String subject = "【" + systemName + "】账户激活邮件";
        String htmlContent = buildActivationEmailHtml(username, activationUrl, systemName);

        sendHtmlEmail(to, subject, htmlContent);
    }

    @Override
    public void sendResetCodeEmail(String to, String code) {
        String systemName = getSystemName();
        String subject = "【" + systemName + "】密码重置验证码";
        String htmlContent = buildResetCodeEmailHtml(code, systemName);
        sendHtmlEmail(to, subject, htmlContent);
    }

    /**
     * 发送HTML格式邮件
     */
    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("邮件发送成功：to={}, subject={}", to, subject);
        } catch (MessagingException e) {
            log.error("邮件发送失败：to={}, error={}", to, e.getMessage());
            throw new RuntimeException("邮件发送失败，请稍后再试", e);
        }
    }

    /**
     * 构建激活邮件HTML内容
     */
    private String buildActivationEmailHtml(String username, String activationUrl, String systemName) {
        return """
                <div style="max-width:600px;margin:0 auto;padding:20px;font-family:Arial,sans-serif;">
                    <div style="background:linear-gradient(135deg,#1890ff,#722ed1);padding:20px;border-radius:8px 8px 0 0;">
                        <h2 style="color:#fff;margin:0;text-align:center;">%s</h2>
                    </div>
                    <div style="border:1px solid #e8e8e8;border-top:none;padding:30px;border-radius:0 0 8px 8px;">
                        <p style="font-size:16px;color:#333;">你好，<strong>%s</strong>：</p>
                        <p style="font-size:14px;color:#666;line-height:1.8;">
                            感谢注册%s！请点击下方按钮激活您的账户：
                        </p>
                        <div style="text-align:center;margin:30px 0;">
                            <a href="%s" target="_blank"
                               style="display:inline-block;padding:12px 40px;
                                      background:linear-gradient(135deg,#1890ff,#722ed1);
                                      color:#fff;text-decoration:none;border-radius:6px;
                                      font-size:16px;font-weight:600;">
                                激活账户
                            </a>
                        </div>
                        <p style="font-size:12px;color:#999;">
                            如果按钮无法点击，请复制以下链接到浏览器地址栏打开：<br/>
                            <span style="color:#1890ff;">%s</span>
                        </p>
                        <p style="font-size:12px;color:#999;margin-top:20px;">
                            此链接在24小时内有效，请尽快完成激活。<br/>
                            如果您未注册此账户，请忽略此邮件。
                        </p>
                    </div>
                </div>
                """.formatted(systemName, username, systemName, activationUrl, activationUrl);
    }

    /**
     * 构建密码重置验证码邮件HTML内容
     */
    private String buildResetCodeEmailHtml(String code, String systemName) {
        return """
                <div style="max-width:600px;margin:0 auto;padding:20px;font-family:Arial,sans-serif;">
                    <div style="background:linear-gradient(135deg,#2563eb,#1d4ed8);padding:20px;border-radius:8px 8px 0 0;">
                        <h2 style="color:#fff;margin:0;text-align:center;">%s - 密码重置</h2>
                    </div>
                    <div style="border:1px solid #e8e8e8;border-top:none;padding:30px;border-radius:0 0 8px 8px;">
                        <p style="font-size:16px;color:#333;">您正在申请重置密码，验证码如下：</p>
                        <div style="text-align:center;margin:30px 0;">
                            <span style="display:inline-block;padding:16px 48px;
                                         background:#f0f5ff;border:2px dashed #2563eb;
                                         color:#2563eb;font-size:32px;font-weight:700;
                                         letter-spacing:8px;border-radius:8px;">
                                %s
                            </span>
                        </div>
                        <p style="font-size:12px;color:#999;">
                            验证码有效期为10分钟，请尽快完成验证。<br/>
                            如果您未申请密码重置，请忽略此邮件。
                        </p>
                    </div>
                </div>
                """.formatted(systemName, code);
    }
}
