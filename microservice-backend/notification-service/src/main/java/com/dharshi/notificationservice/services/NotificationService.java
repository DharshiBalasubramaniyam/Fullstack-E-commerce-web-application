package com.dharshi.notificationservice.services;

import com.dharshi.notificationservice.dtos.ApiResponseDto;
import com.dharshi.notificationservice.dtos.MailRequestDto;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public interface NotificationService {
    ResponseEntity<ApiResponseDto<?>> sendEmail(MailRequestDto requestDto);
}
