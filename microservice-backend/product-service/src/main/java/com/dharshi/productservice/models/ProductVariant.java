package com.dharshi.productservice.models;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
public class ProductVariant {

    @Id
    private Long id;

    private String color;
    private String size;
    private String sku;
    private double price;
}
