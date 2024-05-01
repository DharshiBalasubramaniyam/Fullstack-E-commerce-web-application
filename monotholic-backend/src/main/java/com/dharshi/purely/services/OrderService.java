package com.dharshi.purely.services;

import com.dharshi.purely.dtos.responses.ApiResponseDto;
import com.dharshi.purely.dtos.requests.OrderRequestDto;
import com.dharshi.purely.exceptions.CartNotFoundException;
import com.dharshi.purely.exceptions.OrderNotFoundException;
import com.dharshi.purely.exceptions.ServiceLogicException;
import com.dharshi.purely.exceptions.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface OrderService {
    ResponseEntity<ApiResponseDto<?>> createOrder(OrderRequestDto request) throws UserNotFoundException, ServiceLogicException, CartNotFoundException;

    ResponseEntity<ApiResponseDto<?>> getOrdersByUser(String userId) throws UserNotFoundException, ServiceLogicException;

    ResponseEntity<ApiResponseDto<?>> cancelOrder(String orderId) throws ServiceLogicException, OrderNotFoundException;
}
