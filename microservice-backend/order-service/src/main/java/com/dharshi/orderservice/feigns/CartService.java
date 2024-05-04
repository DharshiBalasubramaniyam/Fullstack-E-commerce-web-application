package com.dharshi.orderservice.feigns;

import com.dharshi.orderservice.dtos.ApiResponseDto;
import com.dharshi.orderservice.dtos.CartDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("CART-SERVICE")
public interface CartService {

    @GetMapping("/cart/get/byId")
    ResponseEntity<ApiResponseDto<CartDto>> getCartById(@RequestParam String id);

    @DeleteMapping("/cart/clear/byId")
    ResponseEntity<ApiResponseDto<?>> clearCartById(@RequestParam String id);

}
