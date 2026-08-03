package com.dharshi.productservice.repositories;

import com.dharshi.productservice.dtos.ProductMcpResponseDto;
import com.dharshi.productservice.models.Product;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public ProductRepositoryCustomImpl(
            MongoTemplate mongoTemplate
    ) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Object searchProducts (
            List<String> keywords,
            Double minPrice,
            Double maxPrice,
            Integer pageNo
    ) {
        int pageSize = 10;
        Query query = new Query();

        List<Criteria> criteriaList = new ArrayList<>();

        // In stock
        criteriaList.add(
                Criteria.where("inStock").is(true)
        );

        // Keywords
        if (keywords != null && !keywords.isEmpty()) {
            List<Criteria> keywordCriteria = new ArrayList<>();

            for (String keyword : keywords) {
                keywordCriteria.add(
                        new Criteria().orOperator(
                                Criteria.where("productName").regex(keyword, "i"),
                                Criteria.where("description").regex(keyword, "i"),
                                Criteria.where("categoryName").regex(keyword, "i")
                        )
                );
            }

            criteriaList.add(
                    new Criteria().orOperator(
                            keywordCriteria.toArray(new Criteria[0])
                    )
            );
        } else {
            return "At least one keyword is required to search products";
        }

        // Min price
        if (minPrice != null) {
            criteriaList.add(
                    Criteria.where("price").gte(minPrice)
            );
        }

        // Max price
        if (maxPrice != null) {
            criteriaList.add(
                    Criteria.where("price").lte(maxPrice)
            );
        }

        query.addCriteria(
                new Criteria().andOperator(
                        criteriaList.toArray(new Criteria[0])
                )
        );

        query.fields()
                .exclude("description")
                .exclude("imageUrl")
                .exclude("categoryId")
                .exclude("categoryName");

        long totalProducts = mongoTemplate.count(query, Product.class);

        // Page
        if (pageNo == null) {
            pageNo = 1;
        }
        long skip = (long) (pageNo-1) * pageSize;
        query.skip(skip);
        query.limit(pageSize);

        List<Product> products = mongoTemplate.find(query, Product.class);

        if (!products.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            List<ProductMcpResponseDto> productsres = new ArrayList<>();
            for (Product p: products) {
                productsres.add(
                        ProductMcpResponseDto.builder()
                                .productId(p.getId())
                                .productName(p.getProductName())
                                .variants(p.getVariants()).build()
                );
            }
            result.put("productsList", productsres);
            result.put("hasMore", Math.ceil((double) totalProducts /pageSize) > pageNo);
            return result;
        } else {
            return "No product matched the provided filters";
        }
    }
}
