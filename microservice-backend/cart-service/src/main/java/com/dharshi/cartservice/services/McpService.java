package com.dharshi.cartservice.services;

import com.dharshi.cartservice.dtos.*;
import com.dharshi.cartservice.feigns.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class McpService {

    private final CartService cartService;
    private final ProductService productService;

    public McpService(CartService cartService, ProductService productService) {
        this.cartService = cartService;
        this.productService = productService;
    }

    @Tool(name = "getCartItemsByUser", description = "Get cart items of an user")
    public Object getCartItemsByUser(
            @ToolParam String userId
    ) {
        log.info("===getCartItemsByUser===");
        try {
            if (userId == null || userId.isEmpty()) {
                return "User id is required to get cart items";
            }

            ResponseEntity<ApiResponseDto<?>> cart = cartService.getCartItemsByUser(userId);

            if (cart.getBody() != null && cart.getBody().getResponse() instanceof CartResponseDto cartResponseDto) {

                return cartResponseDto;

            } else {
                return "Invalid user id or Service unavailable";
            }

        } catch (Exception e) {
            log.debug("[getCartItemsByUser - MCP tool] Error: " + e.getMessage());
            return "Service unavailable";
        }
    }

    @Tool(name = "addItemToCart", description = "Add an new item to a user's cart")
    public Object addItemToCart(
            @ToolParam String userId,
            @ToolParam String productId,
            @ToolParam String sku,
            @ToolParam Integer quantity
    ) {
        log.info("===addItemToCart===");
        try {
            if (userId == null || userId.isEmpty()) {
                return "User id is required to get cart items.";
            }

            if (productId == null || productId.isEmpty()) {
                return "Product id is required to get cart items.";
            }

            if (sku == null || sku.isEmpty()) {
                return "SKU is required to get cart items.";
            }

            if (quantity == null || quantity == 0) {
                return "Quantity is required to get cart items. ";
            }

            ResponseEntity<ApiResponseDto<ProductDto>> product = productService.getProductById(productId);

            if (product.getBody() != null && product.getBody().getResponse() != null) {

                ProductVariantDto variantDto = null;

                for (ProductVariantDto variant : product.getBody().getResponse().getVariants()) {
                    if (variant.getSku().equals(sku)) {
                        variantDto = variant;
                        break;
                    }
                }
                if (variantDto == null) {
                    return "Incorrect SKu: " + sku;
                }

                ResponseEntity<ApiResponseDto<?>> response = cartService.addItemToCart(
                        userId,
                        new CartItemRequestDto(productId, variantDto, quantity)
                );

                if (response.getBody() != null && response.getBody().isSuccess()) {
                    ResponseEntity<ApiResponseDto<?>> cart = cartService.getCartItemsByUser(userId);

                    if (cart.getBody() != null && cart.getBody().getResponse() instanceof CartResponseDto cartResponseDto) {
                        Map<String, Object> result = new HashMap<>();
                        result.put("Message", "Product successfully added to cart");
                        result.put("updatedCart", cartResponseDto);
                        return result;
                    }
                    return "Product successfully added to cart";
                } else {
                    return "Unable to add item to cart";
                }
            } else {
                return "Invalid product id or Service unavailable";
            }
        } catch (Exception e) {
            log.debug("[addItemToCart - MCP tool] Error: " + e.getMessage());
            return "Service unavailable";
        }

    }

    @Tool(name = "updateItemQuantity", description = "Increase the quantity of existing item by adding more items")
    public Object updateItemQuantity(
            @ToolParam String userId,
            @ToolParam String productId,
            @ToolParam String sku,
            @ToolParam Integer quantityToAdd
    ) {
        log.info("===updateItemQuantity===");
        try {
            if (userId == null || userId.isEmpty()) {
                return "User id is required to get cart items.";
            }

            if (productId == null || productId.isEmpty()) {
                return "Product id is required to get cart items.";
            }

            if (sku == null || sku.isEmpty()) {
                return "SKU is required to get cart items.";
            }

            if (quantityToAdd == null || quantityToAdd == 0) {
                return "Quantity is required to get cart items. ";
            }

            ResponseEntity<ApiResponseDto<ProductDto>> product = productService.getProductById(productId);

            if (product.getBody() != null && product.getBody().getResponse() != null) {

                ProductVariantDto variantDto = null;

                for (ProductVariantDto variant : product.getBody().getResponse().getVariants()) {
                    if (variant.getSku().equals(sku)) {
                        variantDto = variant;
                        break;
                    }
                }
                if (variantDto == null) {
                    return "Incorrect SKu: " + sku;
                }

                ResponseEntity<ApiResponseDto<?>> response = cartService.updateQuantity(
                        userId,
                        new CartItemRequestDto(productId, variantDto, quantityToAdd)
                );

                if (response.getBody() != null && response.getBody().isSuccess()) {
                    ResponseEntity<ApiResponseDto<?>> cart = cartService.getCartItemsByUser(userId);

                    if (cart.getBody() != null && cart.getBody().getResponse() instanceof CartResponseDto cartResponseDto) {
                        Map<String, Object> result = new HashMap<>();
                        result.put("Message", "Product quantity updated successfully");
                        result.put("updatedCart", cartResponseDto);
                        return result;
                    }
                    return "Product quantity updated successfully";
                } else {
                    return "Unable to update quantity";
                }
            } else {
                return "Invalid product id or Service unavailable";
            }
        } catch (Exception e) {
            log.debug("[updateItemQuantity - MCP tool] Error: " + e.getMessage());
            return "Service unavailable";
        }

    }

    @Tool(name = "removeItemFromCart", description = "Remove an item from a user's cart")
    public Object removeItemFromCart(
            @ToolParam String userId,
            @ToolParam String productId,
            @ToolParam String sku
    ) {
        log.info("===removeItemFromCart===");
        try {
            if (userId == null || userId.isEmpty()) {
                return "User id is required to get cart items.";
            }

            if (productId == null || productId.isEmpty()) {
                return "Product id is required to get cart items.";
            }

            if (sku == null || sku.isEmpty()) {
                return "SKU is required to get cart items.";
            }

            ResponseEntity<ApiResponseDto<?>> response = cartService.removeCartItemFromCart(
                    userId,
                    productId,
                    sku
            );

            if (response.getBody() != null && response.getBody().isSuccess()) {
                ResponseEntity<ApiResponseDto<?>> cart = cartService.getCartItemsByUser(userId);

                if (cart.getBody() != null && cart.getBody().getResponse() instanceof CartResponseDto cartResponseDto) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("Message", "Product successfully removed from the cart");
                    result.put("updatedCart", cartResponseDto);
                    return result;
                }
                return "Product successfully removed from the cart";
            } else {
                return "Unable to remove item from the cart";
            }

        } catch (Exception e) {
            log.debug("[removeItemFromCart - MCP tool] Error: " + e.getMessage());
            return "Service unavailable";
        }

    }

    @Tool(name = "clearCart", description = "Clear a user's cart")
    public Object clearCart(
            @ToolParam String userId
    ) {
        log.info("===clearCart===");
        try {
            if (userId == null || userId.isEmpty()) {
                return "User id is required to get cart items.";
            }

            ResponseEntity<ApiResponseDto<?>> response = cartService.clearCartByUserId(
                    userId
            );

            if (response.getBody() != null && response.getBody().isSuccess()) {
                ResponseEntity<ApiResponseDto<?>> cart = cartService.getCartItemsByUser(userId);

                if (cart.getBody() != null && cart.getBody().getResponse() instanceof CartResponseDto cartResponseDto) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("Message", "Cart cleared successfully");
                    result.put("updatedCart", cartResponseDto);
                    return result;
                }
                return "Cart cleared successfully";
            } else {
                return "Unable to clear cart";
            }

        } catch (Exception e) {
            log.debug("[clearCart - MCP tool] Error: " + e.getMessage());
            return "Service unavailable";
        }

    }
}
