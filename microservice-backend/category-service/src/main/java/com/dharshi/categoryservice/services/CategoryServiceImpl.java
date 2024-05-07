package com.dharshi.categoryservice.services;

import com.dharshi.categoryservice.dtos.ApiResponseDto;
import com.dharshi.categoryservice.dtos.CategoryRequestDto;
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
    public ResponseEntity<ApiResponseDto<?>> createCategory(CategoryRequestDto categoryRequestDto) throws ServiceLogicException, CategoryAlreadyExistsException {
        try {
            if (!categoryRepository.existsByCategoryName(categoryRequestDto.getName())) {
                Category category = Category.builder()
                        .categoryName(categoryRequestDto.getName())
                        .description(categoryRequestDto.getDescription())
                        .imageUrl(categoryRequestDto.getImageUrl())
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
        throw new CategoryAlreadyExistsException("Category already exists with name " + categoryRequestDto.getName());
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> editCategory(String categoryId, CategoryRequestDto categoryRequestDto) throws ServiceLogicException, CategoryAlreadyExistsException {
        try {
            Category category = categoryRepository.findById(categoryId).orElse(null);
            if (category != null) {
                category.setCategoryName(categoryRequestDto.getName());
                category.setDescription(categoryRequestDto.getDescription());
                category.setImageUrl(categoryRequestDto.getImageUrl());
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
        throw new CategoryNotFoundException("No category found with id " + categoryId);
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
