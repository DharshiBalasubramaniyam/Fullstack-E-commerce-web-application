package com.dharshi.cartservice.dtos;

import lombok.Builder;
import lombok.Data;

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

}
