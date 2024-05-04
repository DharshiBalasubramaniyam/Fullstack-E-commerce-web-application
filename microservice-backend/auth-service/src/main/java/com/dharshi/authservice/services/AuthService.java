package com.dharshi.authservice.services;

import com.dharshi.authservice.dtos.ApiResponseDto;
import com.dharshi.authservice.dtos.SignInRequestDto;
import com.dharshi.authservice.dtos.SignUpRequestDto;
import com.dharshi.authservice.exceptions.ServiceLogicException;
import com.dharshi.authservice.exceptions.UserAlreadyExistsException;
import com.dharshi.authservice.exceptions.UserNotFoundException;
import com.dharshi.authservice.exceptions.UserVerificationFailedException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public interface AuthService {
    ResponseEntity<ApiResponseDto<?>> registerUser(SignUpRequestDto signUpRequestDto) throws UnsupportedEncodingException, UserAlreadyExistsException, ServiceLogicException;
    ResponseEntity<ApiResponseDto<?>> resendVerificationCode(String email) throws UnsupportedEncodingException, UserNotFoundException, ServiceLogicException;
    ResponseEntity<ApiResponseDto<?>> verifyRegistrationVerification(String code) throws UserVerificationFailedException;
    ResponseEntity<ApiResponseDto<?>> authenticateUser(SignInRequestDto signInRequestDto);
    ResponseEntity<ApiResponseDto<?>> validateToken(String token);
}
