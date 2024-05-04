package com.dharshi.notificationservice.controller;

import com.dharshi.notificationservice.dtos.ApiResponseDto;
import com.dharshi.notificationservice.dtos.MailRequestDto;
import com.dharshi.notificationservice.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/send")
    ResponseEntity<ApiResponseDto<?>> sendEmail(@RequestBody MailRequestDto requestDto) {
        return notificationService.sendEmail(requestDto);
    }

}
