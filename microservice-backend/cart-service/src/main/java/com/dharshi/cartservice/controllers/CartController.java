package com.dharshi.cartservice.controllers;

import com.dharshi.cartservice.dtos.ApiResponseDto;
import com.dharshi.cartservice.dtos.CartItemRequestDto;
import com.dharshi.cartservice.exceptions.ResourceNotFoundException;
import com.dharshi.cartservice.exceptions.ServiceLogicException;
import com.dharshi.cartservice.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/purely/cart")
public class CartController {

    // todo: have to implement user authentication(jwt, role) before reaching endpoints, in api gateway

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    ResponseEntity<ApiResponseDto<?>> addItemToCart(@RequestBody CartItemRequestDto requestDto)
            throws ResourceNotFoundException, ServiceLogicException{
        return cartService.addItemToCart(requestDto);
    }

    @GetMapping("/get/byUser")
    ResponseEntity<ApiResponseDto<?>> getCartItemsByUser(@RequestParam String id)
            throws ResourceNotFoundException, ServiceLogicException{
        return cartService.getCartItemsByUser(id);
    }

    @DeleteMapping("/remove")
    ResponseEntity<ApiResponseDto<?>> removeCartItemFromCart(@RequestParam String userId, @RequestParam String productId)
            throws ServiceLogicException, ResourceNotFoundException {
        return cartService.removeCartItemFromCart(userId, productId);
    }

    @GetMapping("/get/byId")
    ResponseEntity<ApiResponseDto<?>> getCartById(@RequestParam String id) throws ServiceLogicException {
        return cartService.getCartById(id);
    }

    @DeleteMapping("/clear/byId")
    ResponseEntity<ApiResponseDto<?>> clearCartById(@RequestParam String id) throws ResourceNotFoundException, ServiceLogicException {
        return cartService.clearCartById(id);
    }

}

