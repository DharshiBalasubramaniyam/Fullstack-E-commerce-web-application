package com.dharshi.orderservice.feigns;

import com.dharshi.orderservice.dtos.ApiResponseDto;
import com.dharshi.orderservice.dtos.InventoryReserveRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient("INVENTORY-SERVICE")
public interface InventoryService {

    @PutMapping("/inventory/reserve/bulk")
    ResponseEntity<ApiResponseDto<Map<String, String>>> reserveInventoryInBulk(@RequestBody List<InventoryReserveRequestDto> inventoryReserveReqList);

    @PutMapping("/inventory/release/bulk")
    ResponseEntity<ApiResponseDto<?>> releaseInventoryInBulk(@RequestBody List<InventoryReserveRequestDto> inventoryReserveReqList);

}
