package com.dharshi.purely.exceptionHandlers;

import com.dharshi.purely.dtos.responses.ApiResponseDto;
import com.dharshi.purely.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<ApiResponseDto<?>> UserNotFoundExceptionHandler(UserNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseDto.builder()
                        .isSuccess(false)
                        .message(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(value = UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponseDto<?>> UserAlreadyExistsExceptionHandler(UserAlreadyExistsException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiResponseDto.builder()
                        .isSuccess(false)
                        .message(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(value = ServiceLogicException.class)
    public ResponseEntity<ApiResponseDto<?>> ServiceLogicExceptionHandler(ServiceLogicException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseDto.builder()
                        .isSuccess(false)
                        .message(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(value = UserVerificationFailedException.class)
    public ResponseEntity<ApiResponseDto<?>> UserVerificationFailedExceptionHandler(UserVerificationFailedException exception) {
        return ResponseEntity.badRequest().body(
                ApiResponseDto.builder()
                        .isSuccess(false)
                        .message(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto<?>> MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException exception) {

        List<String> errorMessage = new ArrayList<>();

        exception.getBindingResult().getFieldErrors().forEach(error -> {
            errorMessage.add(error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(
                ApiResponseDto.builder()
                        .isSuccess(false)
                        .message(errorMessage.toString())
                        .build()
        );
    }

    @ExceptionHandler(value = CategoryNotFoundException.class)
    public ResponseEntity<ApiResponseDto<?>> CategoryNotFoundExceptionHandler(CategoryNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseDto.builder()
                        .isSuccess(false)
                        .message(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(value = CategoryAlreadyExistsException.class)
    public ResponseEntity<ApiResponseDto<?>> CategoryAlreadyExistsExceptionHandler(CategoryAlreadyExistsException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiResponseDto.builder()
                        .isSuccess(false)
                        .message(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(value = ProductNotFoundException.class)
    public ResponseEntity<ApiResponseDto<?>> ProductNotFoundExceptionHandler(ProductNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseDto.builder()
                        .isSuccess(false)
                        .message(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(value = CartNotFoundException.class)
    public ResponseEntity<ApiResponseDto<?>> CartNotFoundExceptionHandler(CartNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseDto.builder()
                        .isSuccess(false)
                        .message(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(value = OrderNotFoundException.class)
    public ResponseEntity<ApiResponseDto<?>> OrderNotFoundExceptionHandler(OrderNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseDto.builder()
                        .isSuccess(false)
                        .message(exception.getMessage())
                        .build()
        );
    }

}
