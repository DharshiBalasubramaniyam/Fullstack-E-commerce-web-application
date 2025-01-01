package com.dharshi.authservice.services;

import com.dharshi.authservice.dtos.*;
import com.dharshi.authservice.exceptions.*;
import com.dharshi.authservice.factories.RoleFactory;
import com.dharshi.authservice.feigns.NotificationService;
import com.dharshi.authservice.modals.Role;
import com.dharshi.authservice.modals.User;
import com.dharshi.authservice.repositories.UserRepository;
import com.dharshi.authservice.security.UserDetailsImpl;
import com.dharshi.authservice.security.UserDetailsServiceImpl;
import com.dharshi.authservice.security.jwt.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AuthServiceImpl implements AuthService {

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

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public ResponseEntity<ApiResponseDto<?>> registerUser(SignUpRequestDto signUpRequestDto)
            throws UserAlreadyExistsException, ServiceLogicException {

        if (userRepository.existsByUsername(signUpRequestDto.getUserName())) {
            throw new UserAlreadyExistsException("Registration Failed: username is already taken!");
        }
        if (userRepository.existsByEmail(signUpRequestDto.getEmail())) {
            throw new UserAlreadyExistsException("Registration Failed: email is already taken!");
        }

        try {
            User user = createUser(signUpRequestDto);
            User savedUser = userRepository.insert(user);

            if (savedUser.getId() != null) {
                try {
                    sendRegistrationVerificationEmail(user);;
                }catch (Exception e) {
                    removeDisabledUser(savedUser.getId());
                    throw new ServiceLogicException("Failed to send verification email.Recheck your email or try again later!");
                }
            }

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
    public ResponseEntity<ApiResponseDto<?>> authenticateUser(SignInRequestDto signInRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signInRequestDto.getEmail(), signInRequestDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        JwtResponseDto jwtResponseDto = JwtResponseDto.builder()
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .id(userDetails.getId())
                .token(jwt)
                .type("Bearer ")
                .roles(roles)
                .build();

        return ResponseEntity.ok(ApiResponseDto.builder()
                .isSuccess(true)
                .message("Authentication successfull!")
                .response(jwtResponseDto)
                .build()
        );
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> validateToken(String token) {
        try {
            if (jwtUtils.validateJwtToken(token)) {
                String username = jwtUtils.getUserNameFromJwtToken(token);

                UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(username);
                List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

                UserAuthorityDto userAuthorityDto = UserAuthorityDto.builder()
                        .userId(userDetails.getId())
                        .authorities(roles)
                        .build();

                return ResponseEntity.ok(
                        ApiResponseDto.builder()
                                .isSuccess(true)
                                .response(userAuthorityDto)
                                .message("Authentication successful!")
                                .build()
                );
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ApiResponseDto.builder()
                            .isSuccess(false)
                            .message("Authentication failed!")
                            .build()
            );
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponseDto.builder()
                            .isSuccess(false)
                            .message("Unable to authorize user right now. Try again later!")
                            .build()
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> resendVerificationCode(String email) throws UserNotFoundException, ServiceLogicException {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found with email " + email));

        try {
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiryTime(calculateCodeExpirationTime());
            user.setEnabled(false);

            userRepository.save(user);
            sendRegistrationVerificationEmail(user);


            return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDto.builder().isSuccess(true)
                    .message("Verification email has been resent successfully!")
                    .build()
            );
        }catch(Exception e) {
            log.error("Registration verification failed: {}", e.getMessage());
            throw new ServiceLogicException("Registration failed: Something went wrong!");
        }

    }

    private void sendRegistrationVerificationEmail(User user) {
        String subject = "Please verify your registration";
        String content = "Dear " + user.getUsername() + ",<br><br>"
                + "<p>Thank you for joining us! We are glad to have you on board.</p><br>"
                + "<p>To complete the sign up process, enter the verification code in your device.</p><br>"
                + "<p>verification code: <strong>" + user.getVerificationCode() + "</strong></p><br>"
                + "<p><strong>Please note that the above verification code will be expired within 15 minutes.</strong></p>"
                + "<br>Thank you,<br>"
                + "Purely.";

        MailRequestDto mail = MailRequestDto.builder()
                .subject(subject)
                .body(content)
                .to(user.getEmail())
                .build();

        notificationService.sendEmail(mail);
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

    private void removeDisabledUser(String id) {
        userRepository.deleteById(id);
    }


}