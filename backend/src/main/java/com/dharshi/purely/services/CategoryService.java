package com.dharshi.purely.services;


import com.dharshi.purely.dtos.reponses.ApiResponseDto;
import com.dharshi.purely.exceptions.CategoryAlreadyExistsException;
import com.dharshi.purely.exceptions.ServiceLogicException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {

    ResponseEntity<ApiResponseDto<?>> getAllCategories() throws ServiceLogicException;

    ResponseEntity<ApiResponseDto<?>> getCategoryById(String categoryId) throws ServiceLogicException;

    ResponseEntity<ApiResponseDto<?>> createCategory(String name) throws ServiceLogicException, CategoryAlreadyExistsException;

}
