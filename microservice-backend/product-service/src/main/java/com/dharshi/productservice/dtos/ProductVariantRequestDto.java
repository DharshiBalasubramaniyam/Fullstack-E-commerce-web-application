package com.dharshi.productservice.dtos;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Builder
public class ProductVariantRequestDto {
    private String productId;
    private String color;
    private String size;
    private String sku;
    private double price;
}
