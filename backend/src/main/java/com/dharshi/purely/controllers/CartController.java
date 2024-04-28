package com.dharshi.purely.controllers;

import com.dharshi.purely.dtos.reponses.ApiResponseDto;
import com.dharshi.purely.dtos.requests.CartItemRequestDto;
import com.dharshi.purely.exceptions.ProductNotFoundException;
import com.dharshi.purely.exceptions.ServiceLogicException;
import com.dharshi.purely.exceptions.UserNotFoundException;
import com.dharshi.purely.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/purely/cart")
@CrossOrigin(origins = "http://localhost:3000")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<ApiResponseDto<?>> addItemToCart(@RequestBody CartItemRequestDto requestDto)
            throws UserNotFoundException, ServiceLogicException, ProductNotFoundException {
        return cartService.addItemToCart(requestDto);
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<ApiResponseDto<?>> getCartItemsByUser(@RequestParam String userId)
            throws UserNotFoundException, ServiceLogicException{
        return cartService.getCartItemsByUser(userId);
    }

    @DeleteMapping("/")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<ApiResponseDto<?>> removeCartItemFromCart(@RequestParam String userId, @RequestParam String productId)
            throws ServiceLogicException{
        return cartService.removeCartItemFromCart(userId, productId);
    }
}

