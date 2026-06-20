package com.dharshi.inventory_service.models;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "inventories")
@Builder
@Data
public class Inventory {

    @Id
    private String id;

    private String sku;

    private String productId;

    private int availableStock;

    private int reservedStock;

}
