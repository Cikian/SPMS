package com.spms.service.impl;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class SendMailMessageService {

    @Retryable(retryFor = MailException.class)
    public void sendEmail(JavaMailSender javaMailSender, String email, String subject, String text) throws MailSendException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("742392696@qq.com");
        message.setTo(email);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }

    @Recover
    public void recover(MailSendException e) {
        System.out.println(e.getMessage());
    }

}
