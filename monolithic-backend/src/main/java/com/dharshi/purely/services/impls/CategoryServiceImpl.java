package com.dharshi.purely.services.impls;

import com.dharshi.purely.dtos.responses.ApiResponseDto;
import com.dharshi.purely.exceptions.CategoryAlreadyExistsException;
import com.dharshi.purely.exceptions.CategoryNotFoundException;
import com.dharshi.purely.exceptions.ServiceLogicException;
import com.dharshi.purely.modals.Category;
import com.dharshi.purely.repositories.CategoryRepository;
import com.dharshi.purely.services.CategoryService;
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
            Category category = null;
            if (categoryRepository.existsById(categoryId)) {
                category = categoryRepository.findById(categoryId).orElse(null);
                return ResponseEntity.ok(
                        ApiResponseDto.builder()
                                .isSuccess(true)
                                .response(category)
                                .build()
                );
            }
        }catch (Exception e) {
            throw new ServiceLogicException("Unable to find categories!");
        }
        throw new CategoryNotFoundException("No category found with id " + categoryId);
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

}
