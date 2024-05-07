package com.dharshi.categoryservice.modals;


import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "categories")
@Builder
public class Category {

    @Id
    private String id;

    private String categoryName;

    private String description;

    private String imageUrl;

}
