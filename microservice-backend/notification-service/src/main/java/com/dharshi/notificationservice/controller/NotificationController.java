package com.dharshi.notificationservice.controller;

import com.dharshi.notificationservice.services.NotificationService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/purely/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/registrationVerification")
    public void sendUserRegistrationVerificationEmail(@RequestParam String email,
                                                      @RequestParam String username,
                                                      @RequestParam String verificationCode)
            throws MessagingException, UnsupportedEncodingException {
        notificationService.sendUserRegistrationVerificationEmail(email, username, verificationCode);
    }

}
