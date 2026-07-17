package com.dharshi.inventory_service.controllers;

import com.dharshi.inventory_service.dtos.ApiResponseDto;
import com.dharshi.inventory_service.dtos.InventoryReserveRequestDto;
import com.dharshi.inventory_service.exceptions.ServiceLogicException;
import com.dharshi.inventory_service.services.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponseDto<?>> getInventoryByProduct(@PathVariable("productId") String productId)
            throws ServiceLogicException {
        return inventoryService.getInventoryByProduct(productId);
    }

    @GetMapping("/product/{productId}/{sku}")
    public ResponseEntity<ApiResponseDto<?>> getInventoryByProduct(@PathVariable("productId") String productId, @PathVariable("sku") String sku)
            throws ServiceLogicException {
        return inventoryService.getInventoryByProductAndSku(productId, sku);
    }

    @PutMapping("/reserve/bulk")
    public ResponseEntity<ApiResponseDto<?>> reserveInventoryInBulk(@RequestBody List<InventoryReserveRequestDto> inventoryReserveReqList)
            throws ServiceLogicException {
        return inventoryService.reserveInventoryInBulk(inventoryReserveReqList);
    }

    @PutMapping("/release/bulk")
    public ResponseEntity<ApiResponseDto<?>> releaseInventoryInBulk(@RequestBody List<InventoryReserveRequestDto> inventoryReserveReqList)
            throws ServiceLogicException {
        return inventoryService.releaseInventoryInBulk(inventoryReserveReqList);
    }

}
