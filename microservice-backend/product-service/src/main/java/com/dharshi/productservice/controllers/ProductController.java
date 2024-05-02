package com.dharshi.productservice.controllers;

import com.dharshi.productservice.dtos.ApiResponseDto;
import com.dharshi.productservice.dtos.ProductRequestDto;
import com.dharshi.productservice.exceptions.ProductNotFoundException;
import com.dharshi.productservice.exceptions.ServiceLogicException;
import com.dharshi.productservice.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/purely/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponseDto<?>> addProduct(@RequestBody ProductRequestDto requestDto) throws ServiceLogicException{
        return productService.addProduct(requestDto);
    }

    @GetMapping("/get/all")
    public ResponseEntity<ApiResponseDto<?>> getAllProducts() throws ServiceLogicException{
        return productService.getAllProducts();
    }

    @GetMapping("/get/byId")
    public ResponseEntity<ApiResponseDto<?>> getProductById(@RequestParam String id) throws ServiceLogicException, ProductNotFoundException{
        return productService.getProductById(id);
    }

    @GetMapping("/get/byCategory")
    public ResponseEntity<ApiResponseDto<?>> getProductByCategory(@RequestParam String id) throws ServiceLogicException{
        return productService.getProductByCategory(id);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponseDto<?>> searchProducts(@RequestParam String searchKey) throws ServiceLogicException{
        return productService.searchProducts(searchKey);
    }


}
