package com.dharshi.cartservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartResponseDto {

    private String cartId;
    private String userId;
    private Set<CartItemResponseDto> cartItems;
    private int noOfCartItems;
    private double subtotal;

}
