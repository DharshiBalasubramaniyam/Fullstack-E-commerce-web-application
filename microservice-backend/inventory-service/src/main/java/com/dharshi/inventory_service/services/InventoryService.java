package com.dharshi.inventory_service.services;

import com.dharshi.inventory_service.daos.ApiResponseDto;
import com.dharshi.inventory_service.exceptions.ServiceLogicException;
import com.dharshi.inventory_service.models.Inventory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface InventoryService {

    ResponseEntity<ApiResponseDto<?>> getInventoryByProducts(String productId) throws ServiceLogicException;
}
