package com.dharshi.authservice.feigns;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("NOTIFICATION-SERVICE")
public interface NotificationService {

    @GetMapping("/purely/notification/registrationVerification")
    public void sendUserRegistrationVerificationEmail(@RequestParam String email,
                                                      @RequestParam String username,
                                                      @RequestParam String verificationCode);
}
