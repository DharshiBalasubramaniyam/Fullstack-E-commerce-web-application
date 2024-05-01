package com.dharshi.purely.controllers;

import com.dharshi.purely.dtos.responses.ApiResponseDto;
import com.dharshi.purely.dtos.requests.CategoryRequestDto;
import com.dharshi.purely.exceptions.CategoryAlreadyExistsException;
import com.dharshi.purely.exceptions.ServiceLogicException;
import com.dharshi.purely.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/purely/category")
@CrossOrigin(origins = "http://localhost:5173")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/get/all")
    public ResponseEntity<ApiResponseDto<?>> getAllCategories() throws ServiceLogicException {
        return categoryService.getAllCategories();
    }

    @GetMapping("/get/byId")
    public ResponseEntity<ApiResponseDto<?>> getCategoryById(@RequestParam String id) throws ServiceLogicException {
        return categoryService.getCategoryById(id);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponseDto<?>> createCategory(@RequestBody CategoryRequestDto categoryRequestDto) throws ServiceLogicException, CategoryAlreadyExistsException {
        return categoryService.createCategory(categoryRequestDto.getName());
    }


}
