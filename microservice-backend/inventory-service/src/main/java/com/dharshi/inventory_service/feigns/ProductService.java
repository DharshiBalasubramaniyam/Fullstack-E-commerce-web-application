package com.dharshi.inventory_service.feigns;

import com.dharshi.inventory_service.dtos.ApiResponseDto;
import com.dharshi.inventory_service.dtos.ProductStockUpdateRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("PRODUCT-SERVICE")
public interface ProductService {

    @PutMapping("/product/stock")
    ResponseEntity<ApiResponseDto<?>> updateStockStatus(@RequestBody ProductStockUpdateRequestDto requestDto);

}
