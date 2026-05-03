package com.dharshi.productservice.controllers;

import com.dharshi.productservice.dtos.ApiResponseDto;
import com.dharshi.productservice.exceptions.ResourceNotFoundException;
import com.dharshi.productservice.exceptions.ServiceLogicException;
import com.dharshi.productservice.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/product")
public class CommonProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/get/all")
    public ResponseEntity<ApiResponseDto<?>> getAllProducts(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "5") int pageSize
    ) throws ServiceLogicException{
        return productService.getAllProducts(
                pageNumber, pageSize
        );
    }

    @GetMapping("/get/byId")
    public ResponseEntity<ApiResponseDto<?>> getProductById(@RequestParam String id) throws ServiceLogicException{
        return productService.getProductById(id);
    }

    @GetMapping("/get/byCategory")
    public ResponseEntity<ApiResponseDto<?>> getProductByCategory(
            @RequestParam String id,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "5") int pageSize) throws ServiceLogicException, ResourceNotFoundException {
        return productService.getProductByCategory(id, pageNumber, pageSize);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponseDto<?>> searchProducts(@RequestParam String searchKey) throws ServiceLogicException{
        return productService.searchProducts(searchKey);
    }


}
