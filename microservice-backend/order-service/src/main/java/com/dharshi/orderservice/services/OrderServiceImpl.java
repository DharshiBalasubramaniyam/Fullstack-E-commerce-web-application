package com.dharshi.orderservice.services;

import com.dharshi.orderservice.dtos.*;
import com.dharshi.orderservice.enums.EOrderPaymentStatus;
import com.dharshi.orderservice.enums.EOrderStatus;
import com.dharshi.orderservice.exceptions.ResourceNotFoundException;
import com.dharshi.orderservice.exceptions.ServiceLogicException;
import com.dharshi.orderservice.feigns.CartService;
import com.dharshi.orderservice.feigns.InventoryService;
import com.dharshi.orderservice.feigns.NotificationService;
import com.dharshi.orderservice.feigns.UserService;
import com.dharshi.orderservice.modals.Order;
import com.dharshi.orderservice.repositories.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

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

    @Autowired
    private InventoryService inventoryService;


    public ResponseEntity<ApiResponseDto<?>> createOrder(String token, String userId, OrderRequestDto request) throws ResourceNotFoundException, ServiceLogicException {

        boolean isInventoryCompleted = false;
        boolean isOrderCompleted = false;
        boolean isCartCompleted = false;
        String orderId = null;
        List<InventoryReserveRequestDto> inventoryReserveReqList = new ArrayList<>();

        try {
            CartDto cart = cartService.getCartById(request.getCartId(), token).getBody().getResponse();

            if (cart != null) {
                try {
                    UserDto user = userService.getUserById(cart.getUserId()).getBody().getResponse();

                    // 1. Reserve inventory
                    inventoryReserveReqList = cart.getCartItems()
                            .stream().map(cartItemDto -> InventoryReserveRequestDto
                                        .builder()
                                        .productId(cartItemDto.getProductId())
                                        .sku(cartItemDto.getVariant().getSku())
                                        .quantity(cartItemDto.getQuantity())
                                        .build()
                            ).toList();
                    ApiResponseDto<Map<String, String>> inventoryResponse =  inventoryService.reserveInventoryInBulk(inventoryReserveReqList).getBody();
                    if (inventoryResponse != null && !inventoryResponse.isSuccess() && inventoryResponse.getMessage().startsWith("OutOfStock")) {
                        String productId = inventoryResponse.getResponse().get("productId");
                        Optional<CartItemDto> item = cart.getCartItems()
                                .stream().filter(cartItemDto -> cartItemDto.getProductId().equals(productId))
                                .findFirst();
                        return ResponseEntity.ok(
                                ApiResponseDto.builder()
                                        .isSuccess(false)
                                        .message("Out of stock")
                                        .response(item)
                                        .build()
                        );

                    }
                    isInventoryCompleted = inventoryResponse != null && inventoryResponse.isSuccess();

                    // 2. Create order
                    Order order = orderRequestDtoToOrder(request, cart);
                    order = orderRepository.insert(order);
                    orderId = order.getId();
                    isOrderCompleted = true;

                    // 3. Clear cart
                    ApiResponseDto<?> cartResponse =  cartService.clearCartById(cart.getCartId(), token).getBody();
                    isCartCompleted = cartResponse != null && cartResponse.isSuccess();

                    // 4. Send notification email
                    sendConfirmationEmail(user, order);

                    return ResponseEntity.ok(
                            ApiResponseDto.builder()
                                    .isSuccess(true)
                                    .message("Order has been successfully placed!")
                                    .build()
                    );
                } catch (Exception e) {
                    if (isInventoryCompleted) {
                        inventoryService.releaseInventoryInBulk(inventoryReserveReqList);
                    }
                    if (isOrderCompleted && orderId != null) {
                        orderRepository.deleteById(orderId);
                    }
                    if (isCartCompleted) {
                        cartService.restoreCart(cart, token);
                    }
                    log.error("Failed to create order: " + e.getMessage());
                    throw new ServiceLogicException("Unable to proceed order!");
                }
            } else {
                throw new ResourceNotFoundException("Cart not found: " + request.getCartId());
            }
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new ServiceLogicException("Error finding cart: " + request.getCartId());
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

            // TODO: Remove reserved products
            // TODO: Send notification email
            // TODO: Restock cart
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