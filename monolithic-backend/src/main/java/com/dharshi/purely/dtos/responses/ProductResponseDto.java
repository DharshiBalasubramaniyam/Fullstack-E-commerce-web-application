package com.dharshi.purely.dtos.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponseDto {

    private String id;

    private String name;

    private double price;

    private String description;

    private String imageUrl;

    private String category;
}
