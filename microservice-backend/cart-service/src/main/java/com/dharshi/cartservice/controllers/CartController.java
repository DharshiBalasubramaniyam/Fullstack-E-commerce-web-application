package com.dharshi.cartservice.controllers;

import com.dharshi.cartservice.dtos.ApiResponseDto;
import com.dharshi.cartservice.dtos.CartItemRequestDto;
import com.dharshi.cartservice.dtos.CartResponseDto;
import com.dharshi.cartservice.exceptions.ResourceNotFoundException;
import com.dharshi.cartservice.exceptions.ServiceLogicException;
import com.dharshi.cartservice.modals.Cart;
import com.dharshi.cartservice.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<ApiResponseDto<?>> addItemToCart(Authentication authentication, @RequestBody CartItemRequestDto requestDto)
            throws ResourceNotFoundException, ServiceLogicException{
        return cartService.addItemToCart(authentication.getPrincipal().toString(), requestDto);
    }

    @PutMapping("/qty")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<ApiResponseDto<?>> updateQuantity(Authentication authentication, @RequestBody CartItemRequestDto requestDto)
            throws ResourceNotFoundException, ServiceLogicException{
        return cartService.updateQuantity(authentication.getPrincipal().toString(), requestDto);
    }

    @GetMapping("/get/byUser")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<ApiResponseDto<?>> getCartItemsByUser(Authentication authentication)
            throws ResourceNotFoundException, ServiceLogicException{
        return cartService.getCartItemsByUser(authentication.getPrincipal().toString());
    }

    @DeleteMapping("/remove")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<ApiResponseDto<?>> removeCartItemFromCart(Authentication authentication, @RequestParam String productId, @RequestParam String sku)
            throws ServiceLogicException, ResourceNotFoundException {
        return cartService.removeCartItemFromCart(authentication.getPrincipal().toString(), productId, sku);
    }

    @GetMapping("/get/byId")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<ApiResponseDto<?>> getCartById(@RequestParam String id) throws ServiceLogicException {
        return cartService.getCartById(id);
    }

    @DeleteMapping("/clear/byId")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<ApiResponseDto<?>> clearCartById(@RequestParam String id) throws ResourceNotFoundException, ServiceLogicException {
        return cartService.clearCartById(id);
    }

    @PutMapping("/restore")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<ApiResponseDto<?>> restoreCart(@RequestBody CartResponseDto cart) throws ResourceNotFoundException, ServiceLogicException {
        return cartService.restoreCart(cart);
    }

}

