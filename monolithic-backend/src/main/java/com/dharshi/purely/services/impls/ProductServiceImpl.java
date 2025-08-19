package com.dharshi.purely.services.impls;

import com.dharshi.purely.dtos.responses.ApiResponseDto;
import com.dharshi.purely.dtos.responses.ProductResponseDto;
import com.dharshi.purely.dtos.requests.ProductRequestDto;
import com.dharshi.purely.exceptions.CategoryNotFoundException;
import com.dharshi.purely.exceptions.ProductNotFoundException;
import com.dharshi.purely.exceptions.ServiceLogicException;
import com.dharshi.purely.modals.Product;
import com.dharshi.purely.repositories.CategoryRepository;
import com.dharshi.purely.repositories.ProductRepository;
import com.dharshi.purely.services.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public ResponseEntity<ApiResponseDto<?>> addProduct(ProductRequestDto requestDto) throws ServiceLogicException {
        try {
            if (categoryRepository.existsById(requestDto.getCategoryId())){
                Product product = productDtoToProduct(requestDto);
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
        throw new CategoryNotFoundException("Category not found with id " + requestDto.getCategoryId());
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getAllProducts() throws ServiceLogicException {
        try {
            List<Product> products = productRepository.findAll();
            List<ProductResponseDto> productResponse = new ArrayList<>();

            for(Product product: products){
                productResponse.add(productToProductDto(product));
            }
            return ResponseEntity.ok(
                    ApiResponseDto.builder()
                            .isSuccess(true)
                            .response(productResponse)
                            .message(products.size() + " results found!")
                            .build()
            );
        }catch (Exception e) {
            throw new ServiceLogicException("Unable to find products!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getProductById(String productId) throws ServiceLogicException, ProductNotFoundException {
        try {
            if (productRepository.existsById(productId)) {
                Product product = productRepository.findById(productId).orElse(null);
                ProductResponseDto productResponse = productToProductDto(product);
                return ResponseEntity.ok(
                        ApiResponseDto.builder()
                                .isSuccess(true)
                                .response(productResponse)
                                .message(" One result found!")
                                .build()
                );
            }

        }catch (Exception e) {
            throw new ServiceLogicException("Unable to find products!");
        }
        throw new ProductNotFoundException("No product found with id " + productId);
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getProductByCategory(String categoryId) throws ServiceLogicException {
        try {
            if (categoryRepository.existsById(categoryId)){
                List<Product> products = productRepository.findByCategory(categoryRepository.findById(categoryId).orElse(null));
                List<ProductResponseDto> productResponse = new ArrayList<>();

                for(Product product: products){
                    productResponse.add(productToProductDto(product));
                }
                return ResponseEntity.ok(
                        ApiResponseDto.builder()
                                .isSuccess(true)
                                .response(productResponse)
                                .message(products.size() + " results found!")
                                .build()
                );
            }

        }catch (Exception e) {
            throw new ServiceLogicException("Unable to find products!");
        }
        throw new CategoryNotFoundException("Category not found with id " + categoryId);
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> searchProducts(String searchKey) throws ServiceLogicException {
        try {
            List<Product> products = productRepository
                    .findByProductNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrCategory_CategoryNameContainingIgnoreCase(searchKey, searchKey, searchKey);
            List<ProductResponseDto> productResponse = new ArrayList<>();

            for(Product product: products){
                productResponse.add(productToProductDto(product));
            }
            return ResponseEntity.ok(
                    ApiResponseDto.builder()
                            .isSuccess(true)
                            .response(productResponse)
                            .message(products.size() + " results found!")
                            .build()
            );

        }catch (Exception e) {
            log.error(e.getMessage());
            throw new ServiceLogicException("Unable to find products!");
        }
    }

    private ProductResponseDto productToProductDto(Product product) {
        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getProductName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .category(product.getCategory().getCategoryName())
                .build();

    }

    private Product productDtoToProduct(ProductRequestDto requestDto) {
        return Product.builder()
                .productName(requestDto.getProductName())
                .price(requestDto.getPrice())
                .description(requestDto.getDescription())
                .imageUrl(requestDto.getImageUrl())
                .category(categoryRepository.findById(requestDto.getCategoryId()).orElse(null))
                .build();
    }

}

