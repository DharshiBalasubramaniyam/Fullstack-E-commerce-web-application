package com.dharshi.productservice.dtos;

import com.dharshi.productservice.models.ProductVariant;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ProductResponseDto {
    private String id;

    private String productName;

    private double price;

    private String description;

    private String imageUrl;

    private String categoryId;

    private String categoryName;

    private List<ProductVariant> variants;

    private List<InventoryDto> inventory;

    private boolean inStock;

}
