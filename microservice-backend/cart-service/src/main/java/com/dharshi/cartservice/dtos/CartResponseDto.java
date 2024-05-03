package com.dharshi.cartservice.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class CartResponseDto {

    private String cartId;
    private String userId;
    private Set<CartItemResponseDto> cartItems;
    private int noOfCartItems;
    private double subtotal;

}
