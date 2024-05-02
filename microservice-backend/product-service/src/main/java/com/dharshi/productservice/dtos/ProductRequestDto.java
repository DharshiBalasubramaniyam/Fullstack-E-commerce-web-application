package com.dharshi.productservice.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ProductRequestDto {

    private String productName;

    private double price;

    private String description;

    private String imageUrl;

    private String categoryId;

}
