package com.ecommerce_project.user_service.controller;

import com.ecommerce_project.user_service.dto.UserCartDeleteRequest;
import com.ecommerce_project.user_service.dto.UserCartRequest;
import com.ecommerce_project.user_service.dto.UserCartResponse;
import com.ecommerce_project.user_service.security.JwtUtil;
import com.ecommerce_project.user_service.service.UserCartService;
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
@RequestMapping("/user/cart")
public class UserCartController {

    private static final Logger logger = LoggerFactory.getLogger(UserCartController.class);

    @Autowired
    private UserCartService userCartService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<UserCartResponse> addToCart(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UserCartRequest request) {

        UUID userId = getUserIdFromToken(authHeader);
        logger.info("Add to cart request for user: {}", userId);

        UserCartResponse response = userCartService.addToCart(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<UserCartResponse>> getCart(
            @RequestHeader("Authorization") String authHeader) {

        UUID userId = getUserIdFromToken(authHeader);
        logger.info("Get cart request for user: {}", userId);

        List<UserCartResponse> responses = userCartService.getCart(userId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping
    public ResponseEntity<UserCartResponse> updateCart(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UserCartRequest request) {

        UUID userId = getUserIdFromToken(authHeader);
        logger.info("Update cart request: {} for user: {}", request.getCartId(), userId);

        UserCartResponse response = userCartService.updateCart(userId, request.getCartId(), request.getQuantity());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<String> removeFromCart(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UserCartDeleteRequest request) {

        UUID userId = getUserIdFromToken(authHeader);
        logger.info("Remove from cart request: {} for user: {}", request.getCartId(), userId);

        String message = userCartService.removeFromCart(userId, request.getCartId());
        return ResponseEntity.ok(message);
    }

    private UUID getUserIdFromToken(String authHeader) {
        String token = authHeader.substring(7);
        String userId = jwtUtil.extractUserId(token);
        return UUID.fromString(userId);
    }
}