package com.dharshi.cartservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartItemRequestDto {
    private String productId;
    private int quantity;
}
