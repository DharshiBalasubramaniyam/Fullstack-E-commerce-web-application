package com.dharshi.cartservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariantDto {

    @Id
    private Long id;

    private String color;
    private String size;
    private String sku;
    private double price;
}