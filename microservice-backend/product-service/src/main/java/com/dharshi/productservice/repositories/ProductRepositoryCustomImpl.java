package com.dharshi.productservice.repositories;

import com.dharshi.productservice.models.Product;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public ProductRepositoryCustomImpl(
            MongoTemplate mongoTemplate
    ) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public String searchProducts (
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
                .include("productName")
                .include("price")
                .include("id");

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
            StringBuilder builder = new StringBuilder();
            builder.append("Showing ").append(skip + 1).append(" - ").append(skip + products.size()).append(" of ").append(totalProducts).append("\n");
            for (Product product: products) {
                builder
                        .append("id = ")
                        .append(product.getId())
                        .append(", product name = ")
                        .append(product.getProductName())
                        .append(", price: ")
                        .append(product.getPrice())
                        .append("\n");
            }

            return builder.toString();
        } else {
            return "No product matched the provided filters";
        }
    }
}
