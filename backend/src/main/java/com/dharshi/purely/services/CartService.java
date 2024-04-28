package com.dharshi.purely.services;

import com.dharshi.purely.dtos.responses.ApiResponseDto;
import com.dharshi.purely.dtos.requests.CartItemRequestDto;
import com.dharshi.purely.exceptions.ProductNotFoundException;
import com.dharshi.purely.exceptions.ServiceLogicException;
import com.dharshi.purely.exceptions.UserNotFoundException;
import org.springframework.http.ResponseEntity;

public interface CartService {
    ResponseEntity<ApiResponseDto<?>> addItemToCart(CartItemRequestDto requestDto) throws UserNotFoundException, ServiceLogicException, ProductNotFoundException;

    ResponseEntity<ApiResponseDto<?>> getCartItemsByUser(String userId) throws UserNotFoundException, ServiceLogicException;

    ResponseEntity<ApiResponseDto<?>> removeCartItemFromCart(String userId, String productId) throws ServiceLogicException;

}
