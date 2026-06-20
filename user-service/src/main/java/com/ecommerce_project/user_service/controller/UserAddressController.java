package com.ecommerce_project.user_service.controller;

import com.ecommerce_project.user_service.dto.UserAddressDeleteRequest;
import com.ecommerce_project.user_service.dto.UserAddressRequest;
import com.ecommerce_project.user_service.dto.UserAddressResponse;
import com.ecommerce_project.user_service.security.JwtUtil;
import com.ecommerce_project.user_service.service.UserAddressService;
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
@RequestMapping("/user/address")
public class UserAddressController {

    private static final Logger logger = LoggerFactory.getLogger(UserAddressController.class);

    @Autowired
    private UserAddressService userAddressService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<UserAddressResponse> addAddress(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UserAddressRequest request) {

        UUID userId = getUserIdFromToken(authHeader);
        logger.info("Add address request for user: {}", userId);

        UserAddressResponse response = userAddressService.addAddress(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<UserAddressResponse>> getAddresses(
            @RequestHeader("Authorization") String authHeader) {

        UUID userId = getUserIdFromToken(authHeader);
        logger.info("Get addresses request for user: {}", userId);

        List<UserAddressResponse> responses = userAddressService.getAddresses(userId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping
    public ResponseEntity<UserAddressResponse> updateAddress(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UserAddressRequest request) {

        UUID userId = getUserIdFromToken(authHeader);
        logger.info("Update address request: {} for user: {}", request.getAddressId(), userId);

        UserAddressResponse response = userAddressService.updateAddress(userId, request.getAddressId(), request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAddress(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UserAddressDeleteRequest request) {

        UUID userId = getUserIdFromToken(authHeader);
        logger.info("Delete address request: {} for user: {}", request.getAddressId(), userId);

        String message = userAddressService.deleteAddress(userId, request.getAddressId());
        return ResponseEntity.ok(message);
    }

    private UUID getUserIdFromToken(String authHeader) {
        String token = authHeader.substring(7);
        String userId = jwtUtil.extractUserId(token);
        return UUID.fromString(userId);
    }
}