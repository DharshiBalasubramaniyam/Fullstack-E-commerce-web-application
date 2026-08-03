package com.dharshi.productservice.services;

import com.dharshi.productservice.dtos.InventoryDto;
import com.dharshi.productservice.feigns.InventoryService;
import com.dharshi.productservice.models.Product;
import com.dharshi.productservice.models.ProductVariant;
import com.dharshi.productservice.repositories.ProductRepository;
import com.dharshi.productservice.repositories.ProductRepositoryCustom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
@Slf4j
public class McpService {

    private final ProductRepositoryCustom productRepositoryCustom;

    private final ProductRepository productRepository;

    private final InventoryService inventoryService;

    public McpService(
            ProductRepositoryCustom productRepositoryCustom,
            ProductRepository productRepository,
            InventoryService inventoryService
    ) {
        this.productRepositoryCustom = productRepositoryCustom;
        this.productRepository = productRepository;
        this.inventoryService = inventoryService;
    }

    @Tool(name = "searchProducts", description = """
    Search products using provided filters, and returns 10 products at a time.
    Use this tool ONCE to find matching products by providing all possible keywords.
    Do not create keywords from assumptions. Use only information provided by the user.
    Generate concise search keywords:
        - Use important product terms only.
        - Remove unnecessary words like:  "I need", "show me", "find", "looking for", "give me".
        - Keep keywords short and meaningful.
    Examples:
    User: "Show me vitamin C supplements"
            
    Correct:
    keywords: ["vitamin c supplement", "vitamin c", "vitamin"]
            
    Incorrect:
    keywords: ["show me vitamin C supplements"]
    
    Never repeat a tool call with similar arguments.
    """)
    public Object searchProducts(
            @ToolParam(description = """
                Keywords to filter products. 
                Filter products which includes at least one keyword in product name, description or category name.
               \s
                Guidelines:
                - Return a list of short search terms, not a sentence.
                - Split long phrases into meaningful keywords.
                - Include important product names, brands, categories, and attributes.
                - Prefer singular nouns when possible.
                - Remove filler words like "I need", "show me", "looking for", etc.
                - Do not include punctuation.
                - Keep each keyword between 1-3 words.
                - Do not combine unrelated concepts into one keyword.
                
               \s
                Examples:
                User: "Looking for Logitech wireless gaming mouse"
                -> ["logitech", "wireless", "gaming", "mouse"]
               \s
                User: "Apple iPhone 15 Pro Max"
                -> ["apple", "iphone", "iphone 15", "pro", "max"]
               \s
                User: "Nike men's running shoes"
                -> ["nike", "men", "running", "shoe"]
           \s"""
            )
            List<String> keywords,
            @ToolParam(required = false) Double minPrice,
            @ToolParam(required = false) Double maxPrice,
            Integer pageNo
    ) {
        log.info("===searchProducts==");
        try {
            return productRepositoryCustom.searchProducts(
                    keywords, minPrice, maxPrice, pageNo
            );
        } catch (Exception e) {
            log.debug("[searchProducts - MCP tool] Error: " + e.getMessage());
            return "Service unavailable";
        }
    }

//    @Tool(name = "getProductVariantsAndInventory", description = "Get price and available count of different variants (size, color) of a product")
    public String getProductVariantsAndInventory(
            @ToolParam String productId

    ) {
        log.info("===getProductVariantsAndInventory==");
        try {
            if (productId == null || productId.isEmpty()) {
                return "Product id is required to get data";
            }
            Optional<Product> product = productRepository.findById(productId);

            if (product.isPresent()) {
                StringBuilder builder = new StringBuilder();
                List<InventoryDto> inventoryList = Objects.requireNonNull(inventoryService.getInventoryByProduct(productId).getBody()).getResponse();
                for (ProductVariant variant: product.get().getVariants()) {
                    builder.append("sku: ").append(variant.getSku()).append(", size: ").append(variant.getSize()).append(", color: ").append(variant.getColor()).append(", price: ").append(variant.getPrice());
                    for (InventoryDto inventoryDto: inventoryList) {
                        if (inventoryDto.getSku().equals(variant.getSku())) {
                            builder.append("in stock: ").append(inventoryDto.getAvailableStock());
                        }
                    }
                }
                return builder.toString();
            }
            return "Product not found with id: " + productId;
        } catch (Exception e) {
            log.debug("[getProductVariantsAndInventory - MCP tool] Error: " + e.getMessage());
            return "Service unavailable";
        }
    }

    @Tool(name = "getProductDescription", description = """
    Get detailed description of a product.
    Use this tool ONLY when user explicitly inquiry about a specific product or ask compare products.
    Use this tools ONCE per product to retrieve it's description.
    If the returned passage do not describe the user's
    question, do NOT call this tool again with a
    rephrased query. Instead, tell the user the
    description does not cover their question and
    suggest they contact support.
    
    Examples queries to invoke this tool:
    User:
    - How to intake this medicine?
    - Can 5 years old child can take in supplement? 
    
    """)
    public String getProductDescription(
            @ToolParam String productId
    ) {
        log.info("===getProductDescription==");
        try {
            if (productId == null || productId.isEmpty()) {
                return "Product id is required to get data";
            }
            Optional<Product> product = productRepository.findById(productId);

            if (product.isPresent()) {
                return product.get().getDescription();
            }
            return "Product not found with id: " + productId;
        }  catch (Exception e) {
            log.debug("[getProductDescription - MCP tool] Error: " + e.getMessage());
            return "Service unavailable";
        }
    }
}
