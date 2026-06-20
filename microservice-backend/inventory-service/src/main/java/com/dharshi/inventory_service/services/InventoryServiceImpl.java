package com.dharshi.inventory_service.services;

import com.dharshi.inventory_service.daos.ApiResponseDto;
import com.dharshi.inventory_service.exceptions.ServiceLogicException;
import com.dharshi.inventory_service.models.Inventory;
import com.dharshi.inventory_service.repositories.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InventoryServiceImpl implements  InventoryService{

    @Autowired
    private InventoryRepository inventoryRepository;

    @Override
    public ResponseEntity<ApiResponseDto<?>> getInventoryByProducts(String productId) throws ServiceLogicException {
        try {
            List<Inventory> inventoryList = inventoryRepository.findByProductId(productId);
            return ResponseEntity.ok(ApiResponseDto
                    .builder()
                    .response(inventoryList)
                    .build()
            );
        } catch (Exception e) {
            throw new ServiceLogicException("Unable fetch inventory. product id " + productId);
        }
    }
}
