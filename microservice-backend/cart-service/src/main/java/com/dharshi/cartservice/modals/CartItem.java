package com.dharshi.cartservice.modals;

import com.dharshi.cartservice.dtos.ProductVariantDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CartItem {

    private String productId;

    private ProductVariantDto variant;

    private int quantity;

}
