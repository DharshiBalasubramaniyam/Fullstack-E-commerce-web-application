package com.dharshi.orderservice.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemDto {
    private String productId;
    private String productName;
    private double price;
    private int quantity;
    private double amount;
    private String imageUrl;
    private String categoryName;

}