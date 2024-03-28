package com.spms.utils;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class SendMailMessageUtils {

    public static void sendEmail(JavaMailSender javaMailSender, String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("742392696@qq.com");
        message.setTo(email);
        message.setSubject("SPMS验证码");
        message.setText("【SPMS】验证码为：" + code + "，5分钟内有效，请勿泄露和转发，如非本人操作，请忽略此短信。");
        javaMailSender.send(message);
    }
}
