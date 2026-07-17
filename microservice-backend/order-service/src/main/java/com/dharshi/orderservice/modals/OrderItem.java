package com.dharshi.orderservice.modals;

import com.dharshi.orderservice.dtos.ProductVariantDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItem {

    private String productId;
    private String productName;
    private double price;
    private int quantity;
    private double amount;
    private String imageUrl;
    private String categoryName;
    private ProductVariantDto variant;
}
