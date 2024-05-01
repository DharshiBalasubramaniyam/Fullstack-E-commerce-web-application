package com.dharshi.purely.services.impls;

import com.dharshi.purely.dtos.responses.ApiResponseDto;
import com.dharshi.purely.dtos.responses.CartResponseDto;
import com.dharshi.purely.dtos.requests.CartItemRequestDto;
import com.dharshi.purely.exceptions.CartNotFoundException;
import com.dharshi.purely.exceptions.ProductNotFoundException;
import com.dharshi.purely.exceptions.ServiceLogicException;
import com.dharshi.purely.exceptions.UserNotFoundException;
import com.dharshi.purely.modals.Cart;
import com.dharshi.purely.modals.CartItem;
import com.dharshi.purely.modals.Product;
import com.dharshi.purely.repositories.CartRepository;
import com.dharshi.purely.repositories.ProductRepository;
import com.dharshi.purely.repositories.UserRepository;
import com.dharshi.purely.services.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class CartServiceImpl implements CartService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Override
    public ResponseEntity<ApiResponseDto<?>> addItemToCart(CartItemRequestDto requestDto) throws UserNotFoundException, ServiceLogicException, ProductNotFoundException {
        try {
            if (!userRepository.existsById(requestDto.getUserId())) {
                throw new UserNotFoundException("User not found with id " + requestDto.getUserId());
            }
            if (!productRepository.existsById(requestDto.getProductId())) {
                throw new ProductNotFoundException("Product not found with id " + requestDto.getProductId());
            }

            Cart userCart = getCart(requestDto.getUserId());
            Set<CartItem> userCartItems = userCart.getCartItems();
            CartItem cartItem = createCartItem(userCartItems,requestDto);

            userCartItems.add(cartItem);
            userCart.setCartItems(userCartItems);

            cartRepository.save(userCart);

            return ResponseEntity.ok(
                    ApiResponseDto.builder()
                            .isSuccess(true)
                            .message("Item successfully added to cart!")
                            .build()
            );
        }catch (UserNotFoundException e) {
            throw new UserNotFoundException(e.getMessage());
        }catch (ProductNotFoundException e) {
            throw new ProductNotFoundException(e.getMessage());
        }catch (Exception e) {
            log.error("Failed to add item to cart: " + e.getMessage());
            throw new ServiceLogicException("Unable to add item to cart!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getCartItemsByUser(String userId) throws UserNotFoundException, ServiceLogicException {
        try {
            if (!userRepository.existsById(userId)) {
                throw new UserNotFoundException("User not found with id " + userId);
            }

            if(!cartRepository.existsByUserId(userId)) {
                createAndSaveNewCart(userId);
            }

            Cart userCart = cartRepository.findByUserId(userId);

            CartResponseDto cartResponse = cartToCartResponseDto(userCart);

            return ResponseEntity.ok(
                    ApiResponseDto.builder()
                            .isSuccess(true)
                            .response(cartResponse)
                            .build()
            );
        }catch (UserNotFoundException e) {
            throw new UserNotFoundException(e.getMessage());
        }catch (Exception e) {
            log.error("Failed to find cart: " + e.getMessage());
            throw new ServiceLogicException("Unable to find cart!");
        }

    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> removeCartItemFromCart(String userId, String productId) throws ServiceLogicException {
        try {
            if (!userRepository.existsById(userId)) {
                throw new UserNotFoundException("User not found with id " + userId);
            }
            if (!productRepository.existsById(productId)) {
                throw new ProductNotFoundException("Product not found with id " + productId);
            }

            Product product = getProduct(productId);

            if(!cartRepository.existsByUserId(userId)) {
                throw new CartNotFoundException("No cart found for user " + userId);
            }

            Cart userCart = cartRepository.findByUserId(userId);
            Set<CartItem> removedItemsSet = removeCartItem(userCart.getCartItems(), product);
            userCart.setCartItems(removedItemsSet);
            cartRepository.save(userCart);

            return ResponseEntity.ok(
                    ApiResponseDto.builder()
                            .isSuccess(true)
                            .message("Item successfully removed to cart!")
                            .build()
            );
        }catch (Exception e) {
            log.error("Failed to add item to cart: " + e.getMessage());
            throw new ServiceLogicException("Unable to add item to cart!");
        }
    }

    private void createAndSaveNewCart(String userId) {
        if(!cartRepository.existsByUserId(userId)) {
            Cart cart = Cart.builder()
                    .userId(userId)
                    .cartItems(new HashSet<>())
                    .build();
            cartRepository.insert(cart);
        }
    }

    private CartItem getNewCartItem(Product product, CartItemRequestDto requestDto) {
        return CartItem.builder()
                .product(productRepository.findById(requestDto.getProductId()).orElse(null))
                .quantity(1)
                .totalPrice(product.getPrice() * requestDto.getQuantity())
                .build();
    }

    private CartItem getExistingCartItem(Set<CartItem> userCartItems, Product product) {
        List<CartItem> existingCartItems = userCartItems.stream().filter(item -> item.getProduct().getId().equals(product.getId())).toList();
        if (existingCartItems.isEmpty()){
            return null;
        }
        return existingCartItems.get(0);
    }

    private Product getProduct(String productId) {
        return productRepository.findById(productId).orElse(null);
    }

    private Cart getCart(String userId) {
        //  if cart is not already present create new cart
        createAndSaveNewCart(userId);
        return cartRepository.findByUserId(userId);
    }

    private CartItem createCartItem(Set<CartItem> userCartItems, CartItemRequestDto requestDto) {

        Product product = getProduct(requestDto.getProductId());
        CartItem cartItem = getExistingCartItem(userCartItems, product);

        if (cartItem == null) {
            cartItem = getNewCartItem(product, requestDto);
        }else {
            if (requestDto.getQuantity() <= 0) requestDto.setQuantity(-1);
            if (requestDto.getQuantity() > 0) requestDto.setQuantity(1);
            if (cartItem.getQuantity() + requestDto.getQuantity() <= 0) requestDto.setQuantity(0);
            userCartItems.remove(cartItem);
            cartItem.setQuantity(cartItem.getQuantity() + requestDto.getQuantity());
            cartItem.setTotalPrice(Double.parseDouble(String.format("%.2f", cartItem.getQuantity() * product.getPrice())));
        }

        return cartItem;
    }

    private CartResponseDto cartToCartResponseDto(Cart userCart) {
        int noOfCartItems = 0;
        double subtotal = 0.0;

        for(CartItem cartItem: userCart.getCartItems()) {
            noOfCartItems += cartItem.getQuantity();
            subtotal += cartItem.getTotalPrice();
        }

        return CartResponseDto.builder()
                .cartId(userCart.getId())
                .cartItems(userCart.getCartItems())
                .noOfCartItems(noOfCartItems)
                .subtotal(subtotal)
                .build();
    }

    private Set<CartItem> removeCartItem(Set<CartItem> userCartItems, Product product) {
        CartItem existingCartItem = getExistingCartItem(userCartItems, product);

        if (existingCartItem != null) {
            userCartItems.remove(existingCartItem);
        }

        return userCartItems;
    }
}
