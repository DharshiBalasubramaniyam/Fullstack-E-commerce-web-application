package com.dharshi.productservice.models;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "products")
public class Product {
    @Id
    private String id;

    private String productName;

    private double price;

    private String description;

    private String imageUrl;

    private String categoryId;

    private String categoryName;

}
