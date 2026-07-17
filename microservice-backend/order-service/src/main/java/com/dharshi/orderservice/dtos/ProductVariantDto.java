package com.dharshi.orderservice.dtos;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Builder
@Data
public class ProductVariantDto {

    @Id
    private Long id;

    private String color;
    private String size;
    private String sku;
    private double price;
}