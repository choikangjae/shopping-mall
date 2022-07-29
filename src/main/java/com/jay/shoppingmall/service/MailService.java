package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.model.NotificationEmail;
import com.jay.shoppingmall.exception.exceptions.MailNotSentException;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@EnableAsync
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${host.url}")
    private String url;

    @Async
    public void sendMail(NotificationEmail notificationEmail) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("shoppingmall@email.com");
            messageHelper.setTo(notificationEmail.getRecipient());
            messageHelper.setSubject(notificationEmail.getSubject());
            messageHelper.setText(notificationEmail.getBody());
        };
    }

    @Async
    public void sendMail(String email, String token) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("비밀번호 초기화");
        simpleMailMessage.setText(url + "auth/reset?email=" + email + "&token=" + token);

        try {
            mailSender.send(simpleMailMessage);
            log.info("Mail sent successfully. email = '{}'", email);
        } catch (MailException e) {
            log.warn("Mail sending failed. message = '{}', email = '{}'", e.getMessage(), email);
            throw new MailNotSentException("Exception occurred when sending mail to " + email);
        }
    }
}