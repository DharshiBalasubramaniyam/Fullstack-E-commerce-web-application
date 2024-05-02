package com.dharshi.notificationservice.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public class EmailNotificationService implements NotificationService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromMail;


    @Override
    public void sendUserRegistrationVerificationEmail(String email, String username, String verificationCode) throws MessagingException, UnsupportedEncodingException {
        String toAddress = email;
        String fromAddress = fromMail;
        String senderName = "Purely";
        String subject = "Please verify your registration";
        String content = "Dear " + username + ",<br><br>"
                + "<p>Thank you for joining us! We are glad to have you on board.</p><br>"
                + "<p>To complete the sign up process, enter the verification code in your device.</p><br>"
                + "<p>verification code: <strong>" + verificationCode + "</strong></p><br>"
                + "<p><strong>Please note that the above verification code will be expired within 15 minutes.</strong></p>"
                + "<br>Thank you,<br>"
                + "Purely.";

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        helper.setText(content, true);

        javaMailSender.send(message);
    }

}
