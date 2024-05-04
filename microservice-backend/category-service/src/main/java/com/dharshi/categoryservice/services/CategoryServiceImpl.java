package com.dharshi.categoryservice.services;

import com.dharshi.categoryservice.dtos.ApiResponseDto;
import com.dharshi.categoryservice.exceptions.CategoryAlreadyExistsException;
import com.dharshi.categoryservice.exceptions.CategoryNotFoundException;
import com.dharshi.categoryservice.exceptions.ServiceLogicException;
import com.dharshi.categoryservice.modals.Category;
import com.dharshi.categoryservice.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public ResponseEntity<ApiResponseDto<?>> getAllCategories() throws ServiceLogicException {
        List<Category> categories = categoryRepository.findAll();
        try {
            return ResponseEntity.ok(
                    ApiResponseDto.builder()
                            .isSuccess(true)
                            .response(categories)
                            .message(categories.size() + " results found!")
                            .build()
            );
        }catch (Exception e) {
            throw new ServiceLogicException("Unable to find categories!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getCategoryById(String categoryId) throws ServiceLogicException {

        try {
            Category category = categoryRepository.findById(categoryId).orElse(null);
            return ResponseEntity.ok(
                    ApiResponseDto.builder()
                            .isSuccess(true)
                            .response(category)
                            .build()
            );
        }catch (Exception e) {
            throw new ServiceLogicException("Unable to find category!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> createCategory(String name) throws ServiceLogicException, CategoryAlreadyExistsException {
        try {
            if (!categoryRepository.existsByCategoryName(name)) {
                Category category = Category.builder()
                        .categoryName(name)
                        .build();
                categoryRepository.insert(category);
                return ResponseEntity.ok(
                        ApiResponseDto.builder()
                                .isSuccess(true)
                                .message("Category saved successfully!")
                                .build()
                );
            }

        }catch (Exception e) {
            throw new ServiceLogicException("Unable save category!");
        }
        throw new CategoryAlreadyExistsException("Category already exists with name " + name);
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> editCategory(String categoryId, String name) throws ServiceLogicException, CategoryAlreadyExistsException {
        try {
            Category category = categoryRepository.findById(categoryId).orElse(null);
            if (category != null && !categoryRepository.existsByCategoryName(name)) {
                category.setCategoryName(name);
                categoryRepository.save(category);
                return ResponseEntity.ok(
                        ApiResponseDto.builder()
                                .isSuccess(true)
                                .message("Category edited successfully!")
                                .build()
                );
            }

        }catch (Exception e) {
            throw new ServiceLogicException("Unable edit category!");
        }
        throw new CategoryAlreadyExistsException("Category already exists with name " + name);
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> deleteCategory(String categoryId) throws ServiceLogicException {
        try {
            if (categoryRepository.existsById(categoryId)) {
                categoryRepository.deleteById(categoryId);
                return ResponseEntity.ok(
                        ApiResponseDto.builder()
                                .isSuccess(true)
                                .message("Category deleted successfully!")
                                .build()
                );
            }

        }catch (Exception e) {
            throw new ServiceLogicException("Unable delete category!");
        }
        throw new CategoryNotFoundException("No category with id " + categoryId);
    }

}
