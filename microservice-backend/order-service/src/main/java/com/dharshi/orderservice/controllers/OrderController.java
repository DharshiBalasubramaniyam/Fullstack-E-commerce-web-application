package com.dharshi.orderservice.controllers;

import com.dharshi.orderservice.dtos.ApiResponseDto;
import com.dharshi.orderservice.dtos.OrderRequestDto;
import com.dharshi.orderservice.exceptions.ResourceNotFoundException;
import com.dharshi.orderservice.exceptions.ServiceLogicException;
import com.dharshi.orderservice.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/purely/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    ResponseEntity<ApiResponseDto<?>> createOrder(@RequestBody OrderRequestDto request) throws ResourceNotFoundException, ServiceLogicException {
        return orderService.createOrder(request);
    }

    @GetMapping("/get/byUser")
    ResponseEntity<ApiResponseDto<?>> getOrdersByUser(@RequestParam String userId) throws ResourceNotFoundException, ServiceLogicException {
        return orderService.getOrdersByUser(userId);
    }

    @PatchMapping("/cancel")
    ResponseEntity<ApiResponseDto<?>> cancelOrder(@RequestParam String orderId) throws ResourceNotFoundException, ServiceLogicException {
        return orderService.cancelOrder(orderId);
    }


}
