package com.dharshi.productservice.exceptionHandlers;

import com.dharshi.productservice.dtos.ApiResponseDto;
import com.dharshi.productservice.exceptions.ResourceNotFoundException;
import com.dharshi.productservice.exceptions.ServiceLogicException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class RestExceptionHandler {


    @ExceptionHandler(value = ServiceLogicException.class)
    public ResponseEntity<ApiResponseDto<?>> ServiceLogicExceptionHandler(ServiceLogicException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseDto.builder()
                        .isSuccess(false)
                        .message(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDto<?>> ResourceNotFoundExceptionHandler(ResourceNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseDto.builder()
                        .isSuccess(false)
                        .message(exception.getMessage())
                        .build()
        );
    }

}
