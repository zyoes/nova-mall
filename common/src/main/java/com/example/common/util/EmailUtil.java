package com.example.common.util;

import cn.hutool.core.util.RandomUtil;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class EmailUtil {
    @Autowired
    JavaMailSender sender;

    @Value("${spring.mail.username}")
    String from;

    /**
     * 注册时发送到邮箱
     * @param email
     */
    public void sendEmailCode(String email,String code) {
        //1. 发送到邮箱
        sendEmail(email, "注册验证码", "本次操作的验证码是：" + code + "(5分钟之内有效)");
    }

    public void sendEmail(String email, String subject, String content){
        // 把验证码发送到对应的邮箱
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setFrom(new InternetAddress(from));
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(content, true);
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        sender.send(message);
    }
}
