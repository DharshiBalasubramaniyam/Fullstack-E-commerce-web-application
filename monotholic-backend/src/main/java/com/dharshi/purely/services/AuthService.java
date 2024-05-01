package com.dharshi.purely.services;

import com.dharshi.purely.dtos.responses.ApiResponseDto;
import com.dharshi.purely.dtos.requests.SignUpRequestDto;
import com.dharshi.purely.exceptions.ServiceLogicException;
import com.dharshi.purely.exceptions.UserAlreadyExistsException;
import com.dharshi.purely.exceptions.UserNotFoundException;
import com.dharshi.purely.exceptions.UserVerificationFailedException;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public interface AuthService {
    ResponseEntity<ApiResponseDto<?>> save(SignUpRequestDto signUpRequestDto) throws MessagingException, UnsupportedEncodingException, UserAlreadyExistsException, ServiceLogicException;
    ResponseEntity<ApiResponseDto<?>> resendVerificationCode(String email) throws MessagingException, UnsupportedEncodingException, UserNotFoundException, ServiceLogicException;

    ResponseEntity<ApiResponseDto<?>> verifyRegistrationVerification(String code) throws UserVerificationFailedException;

}
