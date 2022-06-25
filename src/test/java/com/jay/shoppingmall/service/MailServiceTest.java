package com.jay.shoppingmall.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @InjectMocks
    MailService mailService;
    @Mock
    JavaMailSender mailSender;

    @Test
    void sendMail() {
        mailService.sendMail("email.com", "token");

        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}