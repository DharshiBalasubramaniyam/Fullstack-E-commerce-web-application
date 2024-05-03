package com.dharshi.userservice.services;

import com.dharshi.userservice.dtos.ApiResponseDto;
import com.dharshi.userservice.dtos.UserDto;
import com.dharshi.userservice.exceptions.ServiceLogicException;
import com.dharshi.userservice.exceptions.UserNotFoundException;
import com.dharshi.userservice.modals.User;
import com.dharshi.userservice.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Objects;


@Component
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public ResponseEntity<ApiResponseDto<?>> existsUserById(String userId) throws ServiceLogicException {
        try {
            return ResponseEntity.ok(ApiResponseDto.builder()
                    .isSuccess(true)
                    .response(userRepository.existsById(userId))
                    .message("User exists.")
                    .build());
        }catch(Exception e) {
            log.error(e.getMessage());
            throw new ServiceLogicException("Something went wrong. Try gain later!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getUserById(String id) throws ServiceLogicException, UserNotFoundException {
        try {
            if (userRepository.existsById(id)) {
                return ResponseEntity.ok(ApiResponseDto.builder()
                        .isSuccess(true)
                        .response(userToUserDto(Objects.requireNonNull(userRepository.findById(id).orElse(null))))
                        .build());
            }
        }catch(Exception e) {
            log.error(e.getMessage());
            throw new ServiceLogicException("Something went wrong. Try gain later!");
        }
        throw new UserNotFoundException("User not found with id " + id);
    }

    private UserDto userToUserDto(User user) {
        return UserDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .build();
    }

}