package com.dharshi.purely.services.impls;

import com.dharshi.purely.dtos.reponses.ApiResponseDto;
import com.dharshi.purely.dtos.requests.ResetPasswordRequestDto;
import com.dharshi.purely.dtos.requests.SignUpRequestDto;
import com.dharshi.purely.exceptions.*;
import com.dharshi.purely.factories.RoleFactory;
import com.dharshi.purely.modals.Role;
import com.dharshi.purely.modals.User;
import com.dharshi.purely.repositories.UserRepository;
import com.dharshi.purely.services.AuthService;
import com.dharshi.purely.services.NotificationService;
import com.dharshi.purely.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    RoleFactory roleFactory;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.verificationCodeExpirationMs}")
    private long EXPIRY_PERIOD;

    @Override
    public ResponseEntity<ApiResponseDto<?>> save(SignUpRequestDto signUpRequestDto)
            throws UserAlreadyExistsException, ServiceLogicException {
        if (userService.existsByUsername(signUpRequestDto.getUserName())) {
            throw new UserAlreadyExistsException("Registration Failed: username is already taken!");
        }
        if (userService.existsByEmail(signUpRequestDto.getEmail())) {
            throw new UserAlreadyExistsException("Registration Failed: email is already taken!");
        }

        try {
            User user = createUser(signUpRequestDto);
            userRepository.insert(user);
            notificationService.sendUserRegistrationVerificationEmail(user);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ApiResponseDto.builder().isSuccess(true)
                            .message("User account created successfully!")
                            .build()
            );

        }catch(Exception e) {
            log.error("Registration failed: {}", e.getMessage());
            throw new ServiceLogicException("Registration failed: Something went wrong!");
        }

    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> verifyRegistrationVerification(String code) throws UserVerificationFailedException {
        User user = userRepository.findByVerificationCode(code);

        if (user == null || user.isEnabled()) {
            throw new UserVerificationFailedException("Verification failed: invalid verification code!");
        }

        long currentTimeInMs = System.currentTimeMillis();
        long codeExpiryTimeInMillis = user.getVerificationCodeExpiryTime().getTime();

        if (currentTimeInMs > codeExpiryTimeInMillis) {
            throw new UserVerificationFailedException("Verification failed: expired verification code!");
        }

        user.setVerificationCode(null);
        user.setVerificationCodeExpiryTime(null);
        user.setEnabled(true);
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(
                ApiResponseDto.builder().isSuccess(true)
                        .message("Verification successful: User account has been successfully created!")
                        .build()
        );

    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> resendVerificationCode(String email) throws UserNotFoundException, ServiceLogicException {

        User user = userService.findByEmail(email);

        try {
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiryTime(calculateCodeExpirationTime());
            user.setEnabled(false);

            userRepository.save(user);
            notificationService.sendUserRegistrationVerificationEmail(user);


            return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDto.builder().isSuccess(true)
                    .message("Verification email has been resent successfully!")
                    .build()
            );
        }catch(Exception e) {
            log.error("Registration verification failed: {}", e.getMessage());
            throw new ServiceLogicException("Registration failed: Something went wrong!");
        }

    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> verifyEmailAndSendForgotPasswordVerificationEmail(String email) throws ServiceLogicException, UserNotFoundException {
        if (userService.existsByEmail(email)) {
            try {
                User user = userService.findByEmail(email);
                user.setVerificationCode(generateVerificationCode());
                user.setVerificationCodeExpiryTime(calculateCodeExpirationTime());
                userRepository.save(user);

                notificationService.sendForgotPasswordVerificationEmail(user);

                return ResponseEntity.status(HttpStatus.ACCEPTED).body(
                        ApiResponseDto.builder().isSuccess(true)
                        .message("Verification successful: Email sent successfully!")
                        .build()
                );
            } catch (Exception e) {
                log.error("Reset password email verification failed: {}", e.getMessage());
                throw new ServiceLogicException("Verification failed: Something went wrong!");
            }
        }

        throw new UserNotFoundException("Verification failed: User not found with email " + email + "!");

    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> verifyForgotPasswordVerification(String code) throws UserVerificationFailedException, ServiceLogicException {
        User user = userRepository.findByVerificationCode(code);

        if (user == null) {
            throw new UserVerificationFailedException("Verification failed: invalid verification code!");
        }

        long currentTimeInMs = System.currentTimeMillis();
        long codeExpiryTimeInMillis = user.getVerificationCodeExpiryTime().getTime();

        if (currentTimeInMs > codeExpiryTimeInMillis) {
            throw new UserVerificationFailedException("Verification failed: expired verification code!");
        }

        try {

            user.setVerificationCode(null);
            user.setVerificationCodeExpiryTime(null);
            userRepository.save(user);



            return ResponseEntity.status(HttpStatus.ACCEPTED).body(
                    ApiResponseDto.builder().isSuccess(true)
                            .message("Verification successful: User account has been verified!")
                            .build()
            );
        }catch(Exception e) {
            log.error("Reset password verification failed: {}", e.getMessage());
            throw new ServiceLogicException("Verification failed: Something went wrong!" + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> resetPassword(ResetPasswordRequestDto resetPasswordDto) throws UserNotFoundException, ServiceLogicException {
        if (userService.existsByEmail(resetPasswordDto.getEmail())) {
            try {
                User user = userService.findByEmail(resetPasswordDto.getEmail());

                if (!resetPasswordDto.getCurrentPassword().isEmpty()) {
                    if (!passwordEncoder.matches(resetPasswordDto.getCurrentPassword(), user.getPassword())) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                ApiResponseDto.builder().isSuccess(false)
                                        .message("Reset password not successful: current password is incorrect!!")
                                        .build()
                        );
                    }
                }
                user.setPassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));

                userRepository.save(user);

                return ResponseEntity.status(HttpStatus.CREATED).body(
                        ApiResponseDto.builder().isSuccess(true)
                                .message("Reset successful: Password has been successfully reset!")
                                .build()
                );
            } catch (Exception e) {
                log.error("Resetting password failed: {}", e.getMessage());
                throw new ServiceLogicException("Failed to reset your password: Try again later!");
            }
        }

        throw new UserNotFoundException("User not found with email " + resetPasswordDto.getEmail());
    }

    private User createUser(SignUpRequestDto signUpRequestDto) throws RoleNotFoundException {
        return User.builder()
                .email(signUpRequestDto.getEmail())
                .username(signUpRequestDto.getUserName())
                .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
                .verificationCode(generateVerificationCode())
                .verificationCodeExpiryTime(calculateCodeExpirationTime())
                .enabled(false)
                .roles(determineRoles(signUpRequestDto.getRoles()))
                .build();
    }

    private String generateVerificationCode() {
        return String.valueOf((int) (Math.random() * 1000000));
    }

    private Date calculateCodeExpirationTime() {
        long currentTimeInMs = System.currentTimeMillis();
        return new Date(currentTimeInMs + EXPIRY_PERIOD);
    }

    private Set<Role> determineRoles(Set<String> strRoles) throws RoleNotFoundException {
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            roles.add(roleFactory.getInstance("user"));
        } else {
            for (String role : strRoles) {
                roles.add(roleFactory.getInstance(role));
            }
        }
        return roles;
    }



}