package com.dharshi.productservice.services;

import com.dharshi.productservice.dtos.ApiResponseDto;
import com.dharshi.productservice.dtos.ProductRequestDto;
import com.dharshi.productservice.exceptions.ProductNotFoundException;
import com.dharshi.productservice.exceptions.ServiceLogicException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public interface ProductService {
    ResponseEntity<ApiResponseDto<?>> addProduct(ProductRequestDto requestDto) throws ServiceLogicException;

    ResponseEntity<ApiResponseDto<?>> getAllProducts() throws ServiceLogicException;

    ResponseEntity<ApiResponseDto<?>> getProductById(String productId) throws ServiceLogicException, ProductNotFoundException;

    ResponseEntity<ApiResponseDto<?>> getProductByCategory(String categoryId) throws ServiceLogicException;

    ResponseEntity<ApiResponseDto<?>> searchProducts(String searchKey) throws ServiceLogicException;

}
