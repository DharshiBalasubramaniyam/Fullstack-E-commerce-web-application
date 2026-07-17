package com.dharshi.inventory_service.services;

import com.dharshi.inventory_service.dtos.ApiResponseDto;
import com.dharshi.inventory_service.dtos.InventoryReserveRequestDto;
import com.dharshi.inventory_service.exceptions.ServiceLogicException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface InventoryService {

    ResponseEntity<ApiResponseDto<?>> getInventoryByProduct(String productId) throws ServiceLogicException;

    ResponseEntity<ApiResponseDto<?>> getInventoryByProductAndSku(String productId, String sku) throws ServiceLogicException;

    ResponseEntity<ApiResponseDto<?>> reserveInventoryInBulk(List<InventoryReserveRequestDto> inventoryReserveReqList) throws ServiceLogicException;

    ResponseEntity<ApiResponseDto<?>> releaseInventoryInBulk(List<InventoryReserveRequestDto> inventoryReserveReqList) throws ServiceLogicException;
}
