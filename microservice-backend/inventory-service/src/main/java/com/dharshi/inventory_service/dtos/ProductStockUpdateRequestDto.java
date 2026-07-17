package com.dharshi.inventory_service.dtos;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProductStockUpdateRequestDto {

    private String productId;

    private boolean inStock;

}
