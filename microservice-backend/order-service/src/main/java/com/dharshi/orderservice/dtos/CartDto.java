package com.dharshi.orderservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {

    private String cartId;
    private String userId;
    private Set<CartItemDto> cartItems;
    private int noOfCartItems;
    private double subtotal;

}
