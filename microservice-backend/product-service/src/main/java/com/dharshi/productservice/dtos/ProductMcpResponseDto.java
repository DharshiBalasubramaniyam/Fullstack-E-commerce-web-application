package com.dharshi.productservice.dtos;

import com.dharshi.productservice.models.ProductVariant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductMcpResponseDto {
    String productId;
    String productName;
    List<ProductVariant> variants;
}
