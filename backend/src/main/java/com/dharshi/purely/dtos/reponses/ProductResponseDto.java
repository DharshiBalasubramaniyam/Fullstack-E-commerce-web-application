package com.dharshi.purely.dtos.reponses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponseDto {

    private String id;

    private String name;

    private String price;

    private String description;

    private String imageUrl;

    private String category;
}
