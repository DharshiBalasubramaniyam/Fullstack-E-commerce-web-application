package com.dharshi.notificationservice.services;

import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public interface NotificationService {

    void sendUserRegistrationVerificationEmail(String email, String username, String verificationCode) throws MessagingException, UnsupportedEncodingException;

}
