package com.dharshi.productservice.dtos;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class InventoryDto {

    private String sku;

    private String productId;

    private int availableStock;

    private int reservedStock;
}
