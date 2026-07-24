package com.dharshi.productservice.repositories;

import com.dharshi.productservice.models.Product;

import java.util.List;

public interface ProductRepositoryCustom {
    Object searchProducts(
            List<String> keywords,
            Double minPrice,
            Double maxPrice,
            Integer pageNo
    );
}
