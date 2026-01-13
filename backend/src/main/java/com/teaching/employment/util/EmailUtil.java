package com.teaching.employment.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;

/**
 * 邮件发送工具类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Slf4j
@Component
public class EmailUtil {

    @Value("${spring.mail.username}")
    private String fromEmail;

    private final JavaMailSender mailSender;

    public EmailUtil(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * 发送简单文本邮件
     *
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     * @return 是否发送成功
     */
    public boolean sendSimpleEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            message.setSentDate(new Date());

            mailSender.send(message);
            log.info("简单邮件发送成功: to={}, subject={}", to, subject);
            return true;
        } catch (Exception e) {
            log.error("发送邮件失败: to={}, subject={}, error={}", to, subject, e.getMessage());
            return false;
        }
    }

    /**
     * 发送HTML邮件
     *
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param content HTML内容
     * @return 是否发送成功
     */
    public boolean sendHtmlEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            helper.setSentDate(new Date());

            mailSender.send(message);
            log.info("HTML邮件发送成功: to={}, subject={}", to, subject);
            return true;
        } catch (MessagingException e) {
            log.error("发送HTML邮件失败: to={}, subject={}, error={}", to, subject, e.getMessage());
            return false;
        }
    }

    /**
     * 发送Offer邮件
     *
     * @param to           收件人邮箱
     * @param studentName  学生姓名
     * @param companyName  公司名称
     * @param positionName 职位名称
     * @param salary       薪资
     * @param startDate    入职日期
     * @return 是否发送成功
     */
    public boolean sendOfferEmail(String to, String studentName, String companyName,
                                   String positionName, Double salary, String startDate) {
        String subject = "【录用通知】" + companyName + " - " + positionName;

        String htmlContent = buildOfferEmailTemplate(studentName, companyName, positionName, salary, startDate);

        return sendHtmlEmail(to, subject, htmlContent);
    }

    /**
     * 发送面试通知邮件
     *
     * @param to           收件人邮箱
     * @param studentName  学生姓名
     * @param companyName  公司名称
     * @param positionName 职位名称
     * @param interviewDate 面试日期
     * @param interviewTime 面试时间
     * @param address       面试地址
     * @return 是否发送成功
     */
    public boolean sendInterviewEmail(String to, String studentName, String companyName,
                                      String positionName, String interviewDate, String interviewTime, String address) {
        String subject = "【面试通知】" + companyName + " - " + positionName;

        String htmlContent = buildInterviewEmailTemplate(studentName, companyName, positionName,
                interviewDate, interviewTime, address);

        return sendHtmlEmail(to, subject, htmlContent);
    }

    /**
     * 构建Offer邮件模板
     */
    private String buildOfferEmailTemplate(String studentName, String companyName,
                                           String positionName, Double salary, String startDate) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<title>录用通知</title>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                ".header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }" +
                ".content { padding: 20px; background-color: #f9f9f9; }" +
                ".offer-details { margin: 20px 0; padding: 15px; background-color: white; border-left: 4px solid #4CAF50; }" +
                ".footer { text-align: center; padding: 20px; color: #777; font-size: 12px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h2>🎉 恭喜您被录用!</h2>" +
                "</div>" +
                "<div class='content'>" +
                "<p>尊敬的 <strong>" + studentName + "</strong> 同学:</p>" +
                "<p>经过我们严格的筛选和面试,很高兴地通知您,您已被 <strong>" + companyName + "</strong> 录用!</p>" +
                "<div class='offer-details'>" +
                "<h3>职位详情</h3>" +
                "<p><strong>职位名称:</strong> " + positionName + "</p>" +
                "<p><strong>薪资待遇:</strong> ¥" + (salary != null ? String.format("%.2f", salary) : "面议") + "/月</p>" +
                "<p><strong>入职日期:</strong> " + (startDate != null ? startDate : "待定") + "</p>" +
                "</div>" +
                "<p>请在收到本邮件后3个工作日内确认是否接受此Offer。</p>" +
                "<p>如有任何疑问,请随时与我们联系。</p>" +
                "<p>祝工作顺利!</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>本邮件由系统自动发送,请勿直接回复</p>" +
                "<p>高校教学就业服务平台 © " + java.time.Year.now().getValue() + "</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    /**
     * 构建面试通知邮件模板
     */
    private String buildInterviewEmailTemplate(String studentName, String companyName,
                                               String positionName, String interviewDate,
                                               String interviewTime, String address) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<title>面试通知</title>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                ".header { background-color: #2196F3; color: white; padding: 20px; text-align: center; }" +
                ".content { padding: 20px; background-color: #f9f9f9; }" +
                ".interview-details { margin: 20px 0; padding: 15px; background-color: white; border-left: 4px solid #2196F3; }" +
                ".footer { text-align: center; padding: 20px; color: #777; font-size: 12px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h2>📋 面试通知</h2>" +
                "</div>" +
                "<div class='content'>" +
                "<p>尊敬的 <strong>" + studentName + "</strong> 同学:</p>" +
                "<p>感谢您对 <strong>" + companyName + "</strong> 的关注!我们诚挚地邀请您参加面试。</p>" +
                "<div class='interview-details'>" +
                "<h3>面试详情</h3>" +
                "<p><strong>应聘职位:</strong> " + positionName + "</p>" +
                "<p><strong>面试日期:</strong> " + interviewDate + "</p>" +
                "<p><strong>面试时间:</strong> " + interviewTime + "</p>" +
                "<p><strong>面试地点:</strong> " + address + "</p>" +
                "</div>" +
                "<p>请携带个人简历、成绩单等相关材料准时参加面试。</p>" +
                "<p>如有特殊情况不能按时参加,请提前与我们联系。</p>" +
                "<p>祝面试顺利!</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>本邮件由系统自动发送,请勿直接回复</p>" +
                "<p>高校教学就业服务平台 © " + java.time.Year.now().getValue() + "</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}
