package com.dharshi.productservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PageResponseDto<T> {

    T data;

    int totalNoOfPages;

    Long totalNoOfRecords;

}
