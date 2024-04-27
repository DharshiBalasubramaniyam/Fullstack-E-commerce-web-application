package com.dharshi.purely.controllers;

import com.dharshi.purely.dtos.reponses.ApiResponseDto;
import com.dharshi.purely.dtos.requests.ProductRequestDto;
import com.dharshi.purely.exceptions.ProductNotFoundException;
import com.dharshi.purely.exceptions.ServiceLogicException;
import com.dharshi.purely.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/purely/product")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    ResponseEntity<ApiResponseDto<?>> addProduct(@RequestBody ProductRequestDto requestDto) throws ServiceLogicException{
        return productService.addProduct(requestDto);
    }

    @GetMapping("/all")
    ResponseEntity<ApiResponseDto<?>> getAllProducts() throws ServiceLogicException{
        return productService.getAllProducts();
    }

    @GetMapping("/")
    ResponseEntity<ApiResponseDto<?>> getProductById(@RequestParam String id) throws ServiceLogicException, ProductNotFoundException{
        return productService.getProductById(id);
    }

    @GetMapping("/byCategory")
    ResponseEntity<ApiResponseDto<?>> getProductByCategory(@RequestParam String id) throws ServiceLogicException{
        return productService.getProductByCategory(id);
    }

    @GetMapping("/search")
    ResponseEntity<ApiResponseDto<?>> searchProducts(@RequestParam String searchKey) throws ServiceLogicException {
        return productService.searchProducts(searchKey);
    }

}
