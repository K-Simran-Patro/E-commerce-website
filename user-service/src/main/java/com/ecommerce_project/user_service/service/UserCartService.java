package com.ecommerce_project.user_service.service;

import com.ecommerce_project.user_service.dto.UserCartRequest;
import com.ecommerce_project.user_service.dto.UserCartResponse;
import com.ecommerce_project.user_service.entity.User;
import com.ecommerce_project.user_service.entity.UserCart;
import com.ecommerce_project.user_service.repository.UserCartRepository;
import com.ecommerce_project.user_service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserCartService {

    private static final Logger logger = LoggerFactory.getLogger(UserCartService.class);

    @Autowired
    private UserCartRepository userCartRepository;

    @Autowired
    private UserRepository userRepository;

    // Add to cart
    public UserCartResponse addToCart(UUID userId, UserCartRequest request) {

        logger.info("Adding to cart for user: {}", userId);

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Check if already in cart — update quantity instead
        UserCart existing = userCartRepository.findByUserUserIdAndVariantId(userId, request.getVariantId());

        if (existing != null && existing.getIsActive()) {
            existing.setQuantity(existing.getQuantity() + request.getQuantity());
            existing.setUpdatedAt(OffsetDateTime.now());
            existing.setModifiedBy(user.getEmail());
            userCartRepository.save(existing);
            return mapToResponse(existing);
        }

        UserCart cart = new UserCart();
        cart.setUser(user);
        cart.setVariantId(request.getVariantId());
        cart.setQuantity(request.getQuantity());
        cart.setIsActive(true);
        cart.setCreatedAt(OffsetDateTime.now());
        cart.setUpdatedAt(OffsetDateTime.now());
        cart.setCreatedBy(user.getEmail());
        cart.setModifiedBy(user.getEmail());

        userCartRepository.save(cart);

        logger.info("Added to cart successfully for user: {}", userId);

        return mapToResponse(cart);
    }

    // Get all cart items for a user
    public List<UserCartResponse> getCart(UUID userId) {

        logger.info("Fetching cart for user: {}", userId);

        List<UserCart> carts = userCartRepository.findByUserUserId(userId);
        List<UserCartResponse> responses = new ArrayList<>();

        for (UserCart cart : carts) {
            if (cart.getIsActive()) {
                responses.add(mapToResponse(cart));
            }
        }

        return responses;
    }

    // Update cart quantity
    public UserCartResponse updateCart(UUID userId, UUID cartId, Integer quantity) {

        logger.info("Updating cart: {} for user: {}", cartId, userId);

        UserCart cart = userCartRepository.findById(cartId).orElse(null);

        if (cart == null) {
            throw new RuntimeException("Cart item not found");
        }

        cart.setQuantity(quantity);
        cart.setUpdatedAt(OffsetDateTime.now());
        cart.setModifiedBy(userId.toString());

        userCartRepository.save(cart);

        logger.info("Cart updated successfully: {}", cartId);

        return mapToResponse(cart);
    }

    // Remove from cart — soft delete
    public String removeFromCart(UUID userId, UUID cartId) {

        logger.info("Removing from cart: {} for user: {}", cartId, userId);

        UserCart cart = userCartRepository.findById(cartId).orElse(null);

        if (cart == null) {
            throw new RuntimeException("Cart item not found");
        }

        cart.setIsActive(false);
        cart.setUpdatedAt(OffsetDateTime.now());
        cart.setModifiedBy(userId.toString());

        userCartRepository.save(cart);

        logger.info("Removed from cart successfully: {}", cartId);

        return "Item removed from cart";
    }

    // Map entity to response
    private UserCartResponse mapToResponse(UserCart cart) {
        UserCartResponse response = new UserCartResponse();
        response.setCartId(cart.getCartId());
        response.setVariantId(cart.getVariantId());
        response.setQuantity(cart.getQuantity());
        response.setIsActive(cart.getIsActive());
        return response;
    }
}