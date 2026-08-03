package com.dharshi.orderservice.services;

import com.dharshi.orderservice.dtos.ApiResponseDto;
import com.dharshi.orderservice.dtos.OrderRequestDto;
import com.dharshi.orderservice.dtos.UserDto;
import com.dharshi.orderservice.enums.EOrderStatus;
import com.dharshi.orderservice.feigns.UserService;
import com.dharshi.orderservice.modals.Order;
import com.dharshi.orderservice.repositories.OrderRepository;
import com.dharshi.orderservice.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class McpService {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final JwtUtils jwtUtils;


    @Tool(name = "getOrderByUser", description = "Get order of a user sorted by created datetime in descending order. Returns 10 orders per page.")
    public Object getOrderByUser (
            @ToolParam String userId,
            @ToolParam Integer pageNo

    ) {
        try {
            if (userId == null || userId.isEmpty()) {
                return "User id is required";
            }

            Pageable pageable =  PageRequest.of(pageNo, 10).withSort(Sort.Direction.DESC, "placedOn");
            Page<Order> orderPage = orderRepository.findByUserId(userId, pageable);

            if (orderPage.toList().isEmpty()) {
                return "This user didnt placed any orders yet: ";
            }

            Map<String, Object> result = new HashMap<>();
            result.put("orders", orderPage.getContent());
            result.put("hasMore", pageNo < orderPage.getTotalPages());
            return result;
        } catch (Exception e) {
            log.info("[getOrderByUserMcp] " + e.getMessage());
            return "Service unavailable";
        }
    }

    @Tool(name = "cancelOrder", description = "Cancel order of a user by order number.")
    public Object cancelOrder(
            @ToolParam String orderNumber
    ) {

        try {
            if (orderNumber == null || orderNumber.isEmpty()) {
                return "Order number is required";
            }

            Optional<Order> order = orderRepository.findById(orderNumber);

            if (order.isEmpty()) {
                return "Order not found for given order number: " + orderNumber;
            }
            order.get().setOrderStatus(EOrderStatus.CANCELLED);
            orderRepository.save(order.get());
            return "Order cancelled successfully";
        } catch (Exception e) {
            log.info("[getOrderByUserMcp] " + e.getMessage());
            return "Service unavailable";
        }

    }

    @Tool(name = "createOrder", description = "Place an order")
    public Object createOrder(
            @ToolParam String userId,
            @ToolParam  String firstName,
            @ToolParam  String lastName,
            @ToolParam  String address,
            @ToolParam  String city,
            @ToolParam  String phoneNo,
            @ToolParam  String cartId
    ) {

        try {
            String token = null;
            ApiResponseDto<UserDto> user = userService.getUserById(userId).getBody();
            if (user != null && user.getResponse() != null) {
                token = jwtUtils.generateJwtToken(user.getResponse().getEmail());
            } else {
                return "Invalid user id: " + userId;
            }

            ResponseEntity<ApiResponseDto<?>> response = orderService.createOrder(
                    "Bearer "+token,
                    userId,
                    new OrderRequestDto(
                                firstName,
                                lastName,
                                address,
                                "",
                                city,
                                phoneNo,
                                cartId
                    )
            );

            Map<String, Object> resultMap = new HashMap<>();
            if (response.getBody() != null && response.getBody().isSuccess()) {
                resultMap.put("orderId", response.getBody().getResponse());
                resultMap.put("status", "SUCCESS");
            } else {
                resultMap.put("status", "FAIL");
            }
            return resultMap;
        } catch (Exception e) {
            log.info("[getOrderByUserMcp] " + e.getMessage());
            e.printStackTrace();
            return "Service unavailable";
        }

    }
}
