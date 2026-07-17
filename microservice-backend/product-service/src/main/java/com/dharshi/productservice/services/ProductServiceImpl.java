package com.dharshi.productservice.services;

import com.dharshi.productservice.dtos.*;
import com.dharshi.productservice.exceptions.ResourceAlreadyExistsException;
import com.dharshi.productservice.exceptions.ResourceNotFoundException;
import com.dharshi.productservice.exceptions.ServiceLogicException;
import com.dharshi.productservice.feigns.CategoryService;
import com.dharshi.productservice.feigns.InventoryService;
import com.dharshi.productservice.models.Product;
import com.dharshi.productservice.models.ProductVariant;
import com.dharshi.productservice.repositories.ProductRepository;
import com.netflix.discovery.converters.Auto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private InventoryService inventoryService;

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
    public ResponseEntity<ApiResponseDto<?>> addProductVariant(ProductVariantRequestDto requestDto) throws ResourceNotFoundException, ServiceLogicException {
        try {
            Product product = productRepository.findById(requestDto.getProductId()).orElse(null);
            if (product == null)
                throw new ResourceNotFoundException("Product not found with id " + requestDto.getProductId());

            List<ProductVariant> variants = product.getVariants();

            Optional<ProductVariant> existingV = variants.stream().filter(
                    v -> v.getSize().equals(requestDto.getSize()) && v.getColor().equals(requestDto.getColor())
            ).findFirst();

            if (existingV.isPresent()) {
                throw new ResourceAlreadyExistsException("Variant already exists color = " + requestDto.getColor() + ", size = " + requestDto.getSize());
            }

            ProductVariant newVariant = ProductVariant.builder()
                    .color(requestDto.getColor())
                    .size(requestDto.getSize())
                    .sku(requestDto.getSku())
                    .price(requestDto.getPrice())
                    .build();

            variants.add(newVariant);
            product.setVariants(variants);
            product = productRepository.save(product);

            return ResponseEntity.ok(
                    ApiResponseDto.builder()
                            .isSuccess(true)
                            .message("Product variant saved successfully!")
                            .response(product)
                            .build()
            );
        } catch(ResourceNotFoundException e) {
            throw new ResourceNotFoundException(e.getMessage());
        } catch(Exception e) {
            throw new ServiceLogicException("Unable save variant!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> updateStockStatus(ProductStockUpdateRequestDto requestDto) throws ResourceNotFoundException, ServiceLogicException {
        try {
            Product product = productRepository.findById(requestDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + requestDto.getProductId()));

            product.setInStock(requestDto.isInStock());
            productRepository.save(product);
            return ResponseEntity.ok(
                    ApiResponseDto.builder()
                            .isSuccess(true)
                            .message("Product stock updated successfully!")
                            .response(product)
                            .build()
            );
        } catch(ResourceNotFoundException e) {
            throw new ResourceNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new ServiceLogicException("Unable to find products!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getAllProducts(
            int pageNumber,
            int pageSize
    ) throws ServiceLogicException {
        try {
            Pageable pageable =  PageRequest.of(pageNumber, pageSize).withSort(Sort.Direction.ASC, "productName");
            Page<Product> products = productRepository.findAll(pageable);
            return ResponseEntity.ok(
                    ApiResponseDto.builder()
                            .isSuccess(true)
                            .response(PageResponseDto.builder()
                                    .totalNoOfPages(products.getTotalPages())
                                    .totalNoOfRecords(products.getTotalElements())
                                    .data(products.toList())
                                    .build()
                            )
                            .message(products.toList().size() + " results found!")
                            .build()
            );
        } catch (Exception e) {
            throw new ServiceLogicException("Unable to find products!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getProductById(String productId) throws ServiceLogicException{
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found. product id: " + productId));

            List<InventoryDto> inventoryList = Objects.requireNonNull(inventoryService.getInventoryByProduct(productId).getBody()).getResponse();

            ProductResponseDto productResponseDto = ProductResponseDto
                        .builder()
                        .id(product.getId())
                        .productName(product.getProductName())
                        .price(product.getPrice())
                        .imageUrl(product.getImageUrl())
                        .inStock(product.isInStock())
                        .description(product.getDescription())
                        .categoryId(product.getCategoryId())
                        .categoryName(product.getCategoryName())
                        .variants(product.getVariants())
                        .inventory(inventoryList)
                        .build();

            return ResponseEntity.ok(
                    ApiResponseDto.builder()
                            .isSuccess(true)
                            .response(productResponseDto)
                            .build()
            );

        } catch (Exception e) {
            log.info(e.getMessage());
            e.printStackTrace();
            throw new ServiceLogicException("[getProductById] Unable to find products: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getProductByCategory(String categoryId,
                                                                  int pageNumber,
                                                                  int pageSize) throws ServiceLogicException, ResourceNotFoundException {
        try {
            CategoryDto category = categoryService.getCategoryById(categoryId).getBody().getResponse();

            if (category != null){
                Pageable pageable =  PageRequest.of(pageNumber, pageSize).withSort(Sort.Direction.ASC, "productName");
                Page<Product> products = productRepository.findByCategoryId(categoryId, pageable);

                return ResponseEntity.ok(
                        ApiResponseDto.builder()
                                .isSuccess(true)
                                .response(PageResponseDto.builder()
                                        .totalNoOfPages(products.getTotalPages())
                                        .totalNoOfRecords(products.getTotalElements())
                                        .data(products.toList())
                                        .build()
                                )
                                .message(products.toList().size() + " results found!")
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
                .inStock(requestDto.isInStock())
                .categoryId(categoryDto.getId())
                .categoryName(categoryDto.getCategoryName())
                .build();
    }

}

