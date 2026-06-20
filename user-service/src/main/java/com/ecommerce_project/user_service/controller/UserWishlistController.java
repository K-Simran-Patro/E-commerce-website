package com.ecommerce_project.user_service.controller;

import com.ecommerce_project.user_service.dto.UserWishlistDeleteRequest;
import com.ecommerce_project.user_service.dto.UserWishlistRequest;
import com.ecommerce_project.user_service.dto.UserWishlistResponse;
import com.ecommerce_project.user_service.security.JwtUtil;
import com.ecommerce_project.user_service.service.UserWishlistService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user/wishlist")
public class UserWishlistController {

    private static final Logger logger = LoggerFactory.getLogger(UserWishlistController.class);

    @Autowired
    private UserWishlistService userWishlistService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<UserWishlistResponse> addToWishlist(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UserWishlistRequest request) {

        UUID userId = getUserIdFromToken(authHeader);
        logger.info("Add to wishlist request for user: {}", userId);

        UserWishlistResponse response = userWishlistService.addToWishlist(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<UserWishlistResponse>> getWishlist(
            @RequestHeader("Authorization") String authHeader) {

        UUID userId = getUserIdFromToken(authHeader);
        logger.info("Get wishlist request for user: {}", userId);

        List<UserWishlistResponse> responses = userWishlistService.getWishlist(userId);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping
    public ResponseEntity<String> removeFromWishlist(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UserWishlistDeleteRequest request) {

        UUID userId = getUserIdFromToken(authHeader);
        logger.info("Remove from wishlist request for user: {}", userId);

        String message = userWishlistService.removeFromWishlist(userId, request.getVariantId());
        return ResponseEntity.ok(message);
    }

    private UUID getUserIdFromToken(String authHeader) {
        String token = authHeader.substring(7);
        String userId = jwtUtil.extractUserId(token);
        return UUID.fromString(userId);
    }
}