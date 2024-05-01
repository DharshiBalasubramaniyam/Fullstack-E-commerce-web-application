package com.dharshi.categoryservice.services;


import com.dharshi.categoryservice.dtos.ApiResponseDto;
import com.dharshi.categoryservice.exceptions.CategoryAlreadyExistsException;
import com.dharshi.categoryservice.exceptions.ServiceLogicException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface CategoryService {

    ResponseEntity<ApiResponseDto<?>> getAllCategories() throws ServiceLogicException;

    ResponseEntity<ApiResponseDto<?>> getCategoryById(String categoryId) throws ServiceLogicException;

    ResponseEntity<ApiResponseDto<?>> createCategory(String name) throws ServiceLogicException, CategoryAlreadyExistsException;

}
