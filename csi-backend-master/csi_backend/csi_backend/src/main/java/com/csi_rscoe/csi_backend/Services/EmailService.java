package com.csi_rscoe.csi_backend.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromAddress;

    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        if (fromAddress != null && !fromAddress.isBlank()) {
            message.setFrom(fromAddress);
        }
        message.setSubject(subject);
        message.setText(text);

        if (!emailEnabled) {
            log.info("Email disabled by flag; skipping send to={} subject={}", to, subject);
            return;
        }
        try {
            mailSender.send(message);
            log.info("Email sent successfully to={} subject={} replyTo={} from={}", to, subject, message.getReplyTo(), message.getFrom());
        } catch (MailException ex) {
            log.error("Failed to send email to={} subject={} from={} error={}", to, subject, message.getFrom(), ex.getMessage(), ex);
            throw ex;
        }
    }

    public void sendEmailWithReplyTo(String to, String subject, String text, String replyTo) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        if (fromAddress != null && !fromAddress.isBlank()) {
            message.setFrom(fromAddress);
        }
        if (replyTo != null && !replyTo.isBlank()) {
            message.setReplyTo(replyTo);
        }
        message.setSubject(subject);
        message.setText(text);

        if (!emailEnabled) {
            log.info("Email disabled by flag; skipping send to={} subject={}", to, subject);
            return;
        }
        try {
            mailSender.send(message);
            log.info("Email sent successfully to={} subject={} replyTo={} from={}", to, subject, message.getReplyTo(), message.getFrom());
        } catch (MailException ex) {
            log.error("Failed to send email to={} subject={} from={} error={}", to, subject, message.getFrom(), ex.getMessage(), ex);
            throw ex;
        }
    }

}
