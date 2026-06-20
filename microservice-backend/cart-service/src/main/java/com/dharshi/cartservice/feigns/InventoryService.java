package com.dharshi.cartservice.feigns;

import com.dharshi.cartservice.dtos.ApiResponseDto;
import com.dharshi.cartservice.dtos.InventoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("INVENTORY-SERVICE")
public interface InventoryService {

    @GetMapping("/inventory/product/{productId}/{sku}")
    ResponseEntity<ApiResponseDto<List<InventoryDto>>> getInventoryByProductAndSku(@PathVariable("productId") String productId, @PathVariable("sku") String sku);

}
