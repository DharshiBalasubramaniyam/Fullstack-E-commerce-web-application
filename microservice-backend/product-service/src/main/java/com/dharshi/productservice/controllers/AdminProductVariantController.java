package com.dharshi.productservice.controllers;

import com.dharshi.productservice.dtos.ApiResponseDto;
import com.dharshi.productservice.dtos.ProductRequestDto;
import com.dharshi.productservice.dtos.ProductVariantRequestDto;
import com.dharshi.productservice.exceptions.ResourceNotFoundException;
import com.dharshi.productservice.exceptions.ServiceLogicException;
import com.dharshi.productservice.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/productVariant")
public class AdminProductVariantController {

    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponseDto<?>> addProductVariant(@RequestBody ProductVariantRequestDto requestDto) throws ServiceLogicException, ResourceNotFoundException {
        return productService.addProductVariant(requestDto);
    }

//    @PutMapping("/edit")
//    public ResponseEntity<ApiResponseDto<?>> editProductVariant(@RequestParam String variantId, @RequestBody ProductVariantRequestDto requestDto) throws ServiceLogicException, ResourceNotFoundException {
//        return productService.editProductVariant(variantId, requestDto);
//    }

}
