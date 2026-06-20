package com.dharshi.inventory_service.daos;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ApiResponseDto<T> {
    private boolean isSuccess;
    private String message;
    private T response;
}
