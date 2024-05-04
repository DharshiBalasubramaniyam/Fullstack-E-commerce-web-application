package com.dharshi.productservice.services;

import com.dharshi.productservice.dtos.ApiResponseDto;
import com.dharshi.productservice.dtos.CategoryDto;
import com.dharshi.productservice.dtos.ProductRequestDto;
import com.dharshi.productservice.exceptions.ResourceNotFoundException;
import com.dharshi.productservice.exceptions.ServiceLogicException;
import com.dharshi.productservice.feigns.CategoryService;
import com.dharshi.productservice.models.Product;
import com.dharshi.productservice.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryService categoryService;

    @Override
    public ResponseEntity<ApiResponseDto<?>> addProduct(ProductRequestDto requestDto) throws ServiceLogicException, ResourceNotFoundException {
        try {
            CategoryDto category = categoryService.getCategoryById(requestDto.getCategoryId()).getBody().getResponse();
            if (category != null){
                Product product = productDtoToProduct(requestDto, category);
                productRepository.insert(product);
                return ResponseEntity.ok(
                        ApiResponseDto.builder()
                                .isSuccess(true)
                                .message("Product saved successfully!")
                                .build()
                );
            }
        }catch(Exception e) {
            throw new ServiceLogicException("Unable save category!");
        }
        throw new ResourceNotFoundException("Category not found with id " + requestDto.getCategoryId());
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> editProduct(String productId, ProductRequestDto requestDto) throws ServiceLogicException, ResourceNotFoundException {
        try {

            CategoryDto category = categoryService.getCategoryById(requestDto.getCategoryId()).getBody().getResponse();
            if (category == null)
                throw new ResourceNotFoundException("Category not found with id " + requestDto.getCategoryId());

            Product product = productRepository.findById(productId).orElse(null);
            if (product == null)
                throw new ResourceNotFoundException("Product not found with id " + productId);

            product = productDtoToProduct(requestDto, category);
            product.setId(productId);
            productRepository.save(product);
            return ResponseEntity.ok(
                    ApiResponseDto.builder()
                            .isSuccess(true)
                            .message("Product edited successfully!")
                            .build()
            );
        }catch(ResourceNotFoundException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }catch(Exception e) {
            throw new ServiceLogicException("Unable save category!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getAllProducts() throws ServiceLogicException {
        try {
            List<Product> products = productRepository.findAll();
            return ResponseEntity.ok(
                    ApiResponseDto.builder()
                            .isSuccess(true)
                            .response(products)
                            .message(products.size() + " results found!")
                            .build()
            );
        }catch (Exception e) {
            throw new ServiceLogicException("Unable to find products!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getProductById(String productId) throws ServiceLogicException{
        try {
            Product product = productRepository.findById(productId).orElse(null);

            return ResponseEntity.ok(
                    ApiResponseDto.builder()
                            .isSuccess(true)
                            .response(product)
                            .build()
            );

        }catch (Exception e) {
            throw new ServiceLogicException("Unable to find products!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getProductByCategory(String categoryId) throws ServiceLogicException, ResourceNotFoundException {
        try {
            CategoryDto category = categoryService.getCategoryById(categoryId).getBody().getResponse();

            if (category != null){
                List<Product> products = productRepository.findByCategoryId(categoryId);

                return ResponseEntity.ok(
                        ApiResponseDto.builder()
                                .isSuccess(true)
                                .response(products)
                                .message(products.size() + " results found!")
                                .build()
                );
            }

        }catch (Exception e) {
            throw new ServiceLogicException("Unable to find products!");
        }
        throw new ResourceNotFoundException("Category not found with id " + categoryId);
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> searchProducts(String searchKey) throws ServiceLogicException {
        try {
            List<Product> products = productRepository
                    .findByProductNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrCategoryNameContainingIgnoreCase(searchKey, searchKey, searchKey);

            return ResponseEntity.ok(
                    ApiResponseDto.builder()
                            .isSuccess(true)
                            .response(products)
                            .message(products.size() + " results found!")
                            .build()
            );

        }catch (Exception e) {
            log.error(e.getMessage());
            throw new ServiceLogicException("Unable to find products!");
        }
    }

    private Product productDtoToProduct(ProductRequestDto requestDto, CategoryDto categoryDto) {
        return Product.builder()
                .productName(requestDto.getProductName())
                .price(requestDto.getPrice())
                .description(requestDto.getDescription())
                .imageUrl(requestDto.getImageUrl())
                .categoryId(categoryDto.getId())
                .categoryName(categoryDto.getCategoryName())
                .build();
    }

}

