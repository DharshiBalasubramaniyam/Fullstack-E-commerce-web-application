package com.dharshi.userservice.services;

import com.dharshi.userservice.dtos.ApiResponseDto;
import com.dharshi.userservice.exceptions.ServiceLogicException;
import com.dharshi.userservice.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


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
                    .response(true)
                    .message("User exists.")
                    .build());
        }catch(Exception e) {
            log.error(e.getMessage());
            throw new ServiceLogicException("Something went wrong. Try gain later!");
        }
    }

}