package com.dharshi.productservice.feigns;

import com.dharshi.productservice.dtos.ApiResponseDto;
import com.dharshi.productservice.dtos.CategoryDto;
import com.dharshi.productservice.dtos.InventoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("INVENTORY-SERVICE")
public interface InventoryService {

    @GetMapping("/inventory/product/{productId}")
    ResponseEntity<ApiResponseDto<List<InventoryDto>>> getInventoryByProduct(@PathVariable("productId") String productId);

}
