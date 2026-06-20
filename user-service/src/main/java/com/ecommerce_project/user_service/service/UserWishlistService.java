package com.ecommerce_project.user_service.service;

import com.ecommerce_project.user_service.dto.UserWishlistRequest;
import com.ecommerce_project.user_service.dto.UserWishlistResponse;
import com.ecommerce_project.user_service.entity.User;
import com.ecommerce_project.user_service.entity.UserWishlist;
import com.ecommerce_project.user_service.repository.UserRepository;
import com.ecommerce_project.user_service.repository.UserWishlistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserWishlistService {

    private static final Logger logger = LoggerFactory.getLogger(UserWishlistService.class);

    @Autowired
    private UserWishlistRepository userWishlistRepository;

    @Autowired
    private UserRepository userRepository;

    // Add to wishlist
    public UserWishlistResponse addToWishlist(UUID userId, UserWishlistRequest request) {

        logger.info("Adding to wishlist for user: {}", userId);

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Check if already in wishlist
        UserWishlist existing = userWishlistRepository.findByUserUserIdAndVariantId(userId, request.getVariantId());

        if (existing != null && existing.getIsActive()) {
            throw new RuntimeException("Item already in wishlist");
        }

        UserWishlist wishlist = new UserWishlist();
        wishlist.setUser(user);
        wishlist.setVariantId(request.getVariantId());
        wishlist.setIsActive(true);
        wishlist.setCreatedAt(OffsetDateTime.now());
        wishlist.setUpdatedAt(OffsetDateTime.now());
        wishlist.setCreatedBy(user.getEmail());
        wishlist.setModifiedBy(user.getEmail());

        userWishlistRepository.save(wishlist);

        logger.info("Added to wishlist successfully for user: {}", userId);

        return mapToResponse(wishlist);
    }

    // Get all wishlist items for a user
    public List<UserWishlistResponse> getWishlist(UUID userId) {

        logger.info("Fetching wishlist for user: {}", userId);

        List<UserWishlist> wishlists = userWishlistRepository.findByUserUserId(userId);
        List<UserWishlistResponse> responses = new ArrayList<>();

        for (UserWishlist wishlist : wishlists) {
            if (wishlist.getIsActive()) {
                responses.add(mapToResponse(wishlist));
            }
        }

        return responses;
    }

    // Remove from wishlist — soft delete
    public String removeFromWishlist(UUID userId, Long variantId) {

        logger.info("Removing from wishlist for user: {}", userId);

        UserWishlist wishlist = userWishlistRepository.findByUserUserIdAndVariantId(userId, variantId);

        if (wishlist == null) {
            throw new RuntimeException("Item not found in wishlist");
        }

        wishlist.setIsActive(false);
        wishlist.setUpdatedAt(OffsetDateTime.now());
        wishlist.setModifiedBy(userId.toString());

        userWishlistRepository.save(wishlist);

        logger.info("Removed from wishlist successfully for user: {}", userId);

        return "Item removed from wishlist";
    }

    // Map entity to response
    private UserWishlistResponse mapToResponse(UserWishlist wishlist) {
        UserWishlistResponse response = new UserWishlistResponse();
        response.setWishlistId(wishlist.getWishlistId());
        response.setVariantId(wishlist.getVariantId());
        response.setIsActive(wishlist.getIsActive());
        response.setCreatedAt(wishlist.getCreatedAt());
        return response;
    }
}