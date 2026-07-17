package com.dharshi.inventory_service.services;

import com.dharshi.inventory_service.dtos.ApiResponseDto;
import com.dharshi.inventory_service.dtos.InventoryReserveRequestDto;
import com.dharshi.inventory_service.dtos.ProductStockUpdateRequestDto;
import com.dharshi.inventory_service.exceptions.ServiceLogicException;
import com.dharshi.inventory_service.feigns.ProductService;
import com.dharshi.inventory_service.models.Inventory;
import com.dharshi.inventory_service.repositories.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InventoryServiceImpl implements  InventoryService{

    private static final Logger log = LoggerFactory.getLogger(InventoryServiceImpl.class);
    
    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductService productService;

    @Override
    public ResponseEntity<ApiResponseDto<?>> getInventoryByProduct(String productId) throws ServiceLogicException {
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

    @Override
    public ResponseEntity<ApiResponseDto<?>> getInventoryByProductAndSku(String productId, String sku) throws ServiceLogicException {
        try {
            List<Inventory> inventoryList = inventoryRepository.findByProductIdAndSku(productId, sku);
            return ResponseEntity.ok(ApiResponseDto
                    .builder()
                    .response(inventoryList)
                    .build()
            );
        } catch (Exception e) {
            throw new ServiceLogicException("Unable fetch inventory. product id " + productId);
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> reserveInventoryInBulk(List<InventoryReserveRequestDto> inventoryReserveReqList) throws ServiceLogicException {
        Map<String, String> responseMap = new HashMap<>();
        try {
            for (InventoryReserveRequestDto request: inventoryReserveReqList) {
                List<Inventory> inventoryList = inventoryRepository.findByProductId(request.getProductId());
                if (inventoryList.isEmpty()) {
                    responseMap.put("productId", request.getProductId());
                    responseMap.put("sku", request.getSku());
                    return ResponseEntity.ok(ApiResponseDto
                            .builder()
                            .isSuccess(false)
                            .response(responseMap)
                            .message("Inventory not found. product id: " + request.getProductId())
                            .build()
                    );
                }

                Inventory inventory = null;
                for (Inventory _inventory: inventoryList) {
                    if (_inventory.getSku().equals(request.getSku())) {
                        inventory = _inventory;
                        break;
                    }
                }

                if (inventory != null && inventory.getAvailableStock() >= request.getQuantity()) {
                    inventory.setAvailableStock(inventory.getAvailableStock() - request.getQuantity());
                    inventory.setReservedStock(inventory.getReservedStock() + request.getQuantity());
                    inventoryRepository.save(inventory);
                    int totalStockAfterUpdate = getTotalStock(inventoryList);
                    if (totalStockAfterUpdate == 0) {
                        boolean stockUpdateFlag = updateProductInStockStatus(inventory.getProductId(), false);
                        if (!stockUpdateFlag) {
                            // Roll back inventoryRepository.save(inventory) if needed
                        }
                    }
                } else  {
                    responseMap.put("productId", request.getProductId());
                    responseMap.put("sku", request.getSku());
                    return ResponseEntity.ok(ApiResponseDto
                            .builder()
                            .isSuccess(false)
                            .response(responseMap)
                            .message("OutOfStock: " + request.getProductId())
                            .build()
                    );
                }
            }

            return ResponseEntity.ok(ApiResponseDto
                    .builder()
                    .isSuccess(true)
                    .build()
            );
        } catch (Exception e) {
            throw new ServiceLogicException("Unable Reserve inventory.");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> releaseInventoryInBulk(List<InventoryReserveRequestDto> inventoryReserveReqList) throws ServiceLogicException {
        try {
            for (InventoryReserveRequestDto request: inventoryReserveReqList) {
                List<Inventory> inventoryList = inventoryRepository.findByProductId(request.getProductId());

                if (!inventoryList.isEmpty()) {
                    Inventory inventory = null;
                    for (Inventory _inventory: inventoryList) {
                        if (_inventory.getSku().equals(request.getSku())) {
                            inventory = _inventory;
                            break;
                        }
                    }
                    if (inventory != null) {
                        int totalStockBeforeUpdate = getTotalStock(inventoryList);
                        inventory.setAvailableStock(inventory.getAvailableStock() + request.getQuantity());
                        inventory.setReservedStock(inventory.getReservedStock() - request.getQuantity());
                        inventoryRepository.save(inventory);
                        if (totalStockBeforeUpdate == 0) {
                            boolean stockUpdateFlag = updateProductInStockStatus(inventory.getProductId(), true);
                            if (!stockUpdateFlag) {
                                // Roll back inventoryRepository.save(inventory) if needed
                            }
                        }
                    } else {
                        return ResponseEntity.ok(ApiResponseDto
                                .builder()
                                .isSuccess(false)
                                .message("Out of stock")
                                .build()
                        );
                    }
                } else {
                    return ResponseEntity.ok(ApiResponseDto
                            .builder()
                            .isSuccess(false)
                            .message("Inventory not found. product id: " + request.getProductId())
                            .build()
                    );
                }
            }

            return ResponseEntity.ok(ApiResponseDto
                    .builder()
                    .isSuccess(true)
                    .build()
            );
        } catch (Exception e) {
            throw new ServiceLogicException("Unable Reserve inventory.");
        }
    }

    private boolean updateProductInStockStatus(String productId, boolean inStock) {
        try {
            ApiResponseDto<?> response = productService.updateStockStatus(
                        ProductStockUpdateRequestDto
                                .builder()
                                .productId(productId)
                                .inStock(inStock)
                                .build()
            ).getBody();
            if (response != null && !response.isSuccess()) {
                log.info("[checkAndUpdateProductInStockStatus] Error: " + response.getMessage());
                return response.isSuccess();
            }
            log.info("[checkAndUpdateProductInStockStatus] Error: Null response ");
            return false;
        } catch (Exception e) {
            log.info("[checkAndUpdateProductInStockStatus] Error: " + e.getMessage());
            return false;
        }
    }

    private static int getTotalStock(List<Inventory> inventoryList) {
        int totalStock = 0;
        for (Inventory _inventory: inventoryList) {
            totalStock += _inventory.getAvailableStock();
        }
        return totalStock;
    }
}
