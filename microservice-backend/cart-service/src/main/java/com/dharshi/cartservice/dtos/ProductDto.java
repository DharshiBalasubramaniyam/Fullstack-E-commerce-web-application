package com.dharshi.cartservice.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProductDto {

    private String id;

    private String productName;

    private double price;

    private String description;

    private String imageUrl;

    private String categoryId;

    private String categoryName;

    private boolean inStock;

    private List<ProductVariantDto> variants;


}
