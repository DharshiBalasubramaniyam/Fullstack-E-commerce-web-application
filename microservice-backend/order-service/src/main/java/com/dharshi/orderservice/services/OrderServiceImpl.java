package com.dharshi.orderservice.services;

import com.dharshi.orderservice.dtos.*;
import com.dharshi.orderservice.enums.EOrderPaymentStatus;
import com.dharshi.orderservice.enums.EOrderStatus;
import com.dharshi.orderservice.exceptions.ResourceNotFoundException;
import com.dharshi.orderservice.exceptions.ServiceLogicException;
import com.dharshi.orderservice.feigns.CartService;
import com.dharshi.orderservice.feigns.NotificationService;
import com.dharshi.orderservice.feigns.UserService;
import com.dharshi.orderservice.modals.Order;
import com.dharshi.orderservice.repositories.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;


    public ResponseEntity<ApiResponseDto<?>> createOrder(String token, OrderRequestDto request) throws ResourceNotFoundException, ServiceLogicException {

        try {

            CartDto cart = cartService.getCartById(request.getCartId(), token).getBody().getResponse();
            UserDto user = userService.getUserById(cart.getUserId()).getBody().getResponse();

            if (user==null || cart == null || cart.getCartItems().isEmpty()) {
                throw new ResourceNotFoundException("No items in the cart!");
            }

            Order order = orderRequestDtoToOrder(request, cart);
            order = orderRepository.insert(order);
            try {
                if (order.getId() != null && clearCart(cart, token) && sendConfirmationEmail(user, order)) {
                    return ResponseEntity.ok(
                            ApiResponseDto.builder()
                                    .isSuccess(true)
                                    .message("Order has been successfully placed!")
                                    .build()
                    );
                }
                throw new ServiceLogicException("Unable to proceed order!");
            }catch (Exception e) {
                orderRepository.deleteById(order.getId());
                throw new ServiceLogicException(e.getMessage());
            }

        }catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }catch (Exception e) {
            log.error("Failed to create order: " + e.getMessage());
            throw new ServiceLogicException("Unable to proceed order!");
        }
    }

    public ResponseEntity<ApiResponseDto<?>> getOrdersByUser(String userId) throws ServiceLogicException {
        try {
            Set<Order> orders = orderRepository.findByUserIdOrderByIdDesc(userId);
            return ResponseEntity.ok(
                    ApiResponseDto.builder()
                            .isSuccess(true)
                            .message(orders.size() + " orders found!")
                            .response(orders)
                            .build()
            );
        }catch (Exception e) {
            log.error("Failed to create order: " + e.getMessage());
            throw new ServiceLogicException("Unable to find orders!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getAllOrders() throws ServiceLogicException {
        try {
            List<Order> orders = orderRepository.findAll();
            return ResponseEntity.ok(
                    ApiResponseDto.builder()
                            .isSuccess(true)
                            .message(orders.size() + " orders found!")
                            .response(orders)
                            .build()
            );
        }catch (Exception e) {
            log.error("Failed to create order: " + e.getMessage());
            throw new ServiceLogicException("Unable to find orders!");
        }
    }


    @Override
    public ResponseEntity<ApiResponseDto<?>> cancelOrder(String orderId) throws ServiceLogicException, ResourceNotFoundException {
        try {
            if(orderRepository.existsById(orderId)) {
                Order order = orderRepository.findById(orderId).orElse(null);
                order.setOrderStatus(EOrderStatus.CANCELLED);
                orderRepository.save(order);
                return ResponseEntity.ok(
                        ApiResponseDto.builder()
                                .isSuccess(true)
                                .message("Order successfully cancelled")
                                .build()
                );
            }
        }catch (Exception e) {
            log.error("Failed to create order: " + e.getMessage());
            throw new ServiceLogicException("Unable to find orders!");
        }
        throw new ResourceNotFoundException("Order not found with id " + orderId);
    }

    private boolean clearCart(CartDto cart, String token) {
        return Objects.requireNonNull(cartService.clearCartById(cart.getCartId(), token).getBody()).isSuccess();
    }

    private boolean sendConfirmationEmail(UserDto user, Order order) {
        StringBuilder contentBuilder = new StringBuilder("Dear " + user.getUsername() + ",<br><br>"
                + "<h2>Thank you for your order!</h2>"
                + "<p>Your order #" + order.getId() + " has been successfully placed!</p>"
                + "<h3>Order summary</h3>");
        for( CartItemDto item: order.getOrderItems()) {
            String description = item.getProductName() + ": " + item.getQuantity() + " x " + item.getPrice() + "<br>";
            contentBuilder.append(description);
        }

        String content = contentBuilder.toString();

        content += "<h4>Total: " + order.getOrderAmt() + "</h4>"
                + "<p>Delivery charges be will added to your total at your doorstep!</p>"
                + "<br>Thank you,<br>"
                + "Purely.";

        MailRequestDto mail = MailRequestDto.builder()
                .to(user.getEmail())
                .subject("Purely - Order confirmation")
                .body(content)
                .build();

        return notificationService.sendEmail(mail).getBody().isSuccess();
    }

    private Order orderRequestDtoToOrder(OrderRequestDto request, CartDto cart) {
        return Order.builder()
                .userId(cart.getUserId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .phoneNo(request.getPhoneNo())
                .placedOn(LocalDateTime.now())
                .orderStatus(EOrderStatus.PENDING)
                .paymentStatus(EOrderPaymentStatus.UNPAID)
                .orderAmt(cart.getSubtotal())
                .orderItems(cart.getCartItems())
                .build();
    }

}
