package com.dharshi.orderservice.dtos;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class InventoryReserveRequestDto {

    private String sku;

    private String productId;

    private int quantity;

}
