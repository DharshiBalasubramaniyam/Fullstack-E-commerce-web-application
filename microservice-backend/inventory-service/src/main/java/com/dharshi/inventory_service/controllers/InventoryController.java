package com.dharshi.inventory_service.controllers;

import com.dharshi.inventory_service.daos.ApiResponseDto;
import com.dharshi.inventory_service.exceptions.ServiceLogicException;
import com.dharshi.inventory_service.services.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponseDto<?>> getInventoryByProduct(@PathVariable("productId") String productId)
            throws ServiceLogicException {
        return inventoryService.getInventoryByProducts(productId);
    }

}
