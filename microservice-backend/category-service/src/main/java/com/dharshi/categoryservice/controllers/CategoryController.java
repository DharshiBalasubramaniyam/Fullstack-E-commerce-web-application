package com.dharshi.categoryservice.controllers;

import com.dharshi.categoryservice.dtos.ApiResponseDto;
import com.dharshi.categoryservice.dtos.CategoryRequestDto;
import com.dharshi.categoryservice.exceptions.CategoryAlreadyExistsException;
import com.dharshi.categoryservice.exceptions.ServiceLogicException;
import com.dharshi.categoryservice.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/purely/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/get")
    public ResponseEntity<ApiResponseDto<?>> getCat() throws ServiceLogicException {
        return categoryService.getAllCategories();
    }

    @GetMapping("/get/all")
    public ResponseEntity<ApiResponseDto<?>> getAllCategories() throws ServiceLogicException {
        return categoryService.getAllCategories();
    }

    @GetMapping("/get/byId")
    public ResponseEntity<ApiResponseDto<?>> getCategoryById(@RequestParam String id) throws ServiceLogicException {
        return categoryService.getCategoryById(id);
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponseDto<?>> createCategory(@RequestBody CategoryRequestDto categoryRequestDto) throws ServiceLogicException, CategoryAlreadyExistsException {
        return categoryService.createCategory(categoryRequestDto.getName());
    }


}
