package com.dharshi.productservice.dtos;


import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "categories")
@Builder
public class CategoryDto {

    @Id
    private String id;

    private String categoryName;

}
