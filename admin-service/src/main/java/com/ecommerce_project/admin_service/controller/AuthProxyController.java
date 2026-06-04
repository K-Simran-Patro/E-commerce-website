package com.ecommerce_project.admin_service.controller;

import com.ecommerce_project.admin_service.dto.LoginRequestDTO;
import com.ecommerce_project.admin_service.dto.RegisterRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Login and Register endpoints")
public class AuthProxyController {

    private static final Logger logger = LoggerFactory.getLogger(AuthProxyController.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${user.service.url}")
    private String userServiceUrl;

    // ─── Login ────────────────────────────────────────────
    @PostMapping("/login")
    @Operation(summary = "Admin login", description = "Forwards login request to User Service and returns JWT token")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {

        logger.info("Login request received for username: {}", loginRequestDTO.getUsername());

        String url = userServiceUrl + "/auth/login";

        ResponseEntity<Object> response = restTemplate.postForEntity(url, loginRequestDTO, Object.class);

        logger.info("Login response received from User Service for username: {}", loginRequestDTO.getUsername());

        return response;
    }

    // ─── Register ─────────────────────────────────────────
    @PostMapping("/register")
    @Operation(summary = "Register new admin", description = "Forwards register request to User Service to create a new admin")
    public ResponseEntity<Object> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {

        logger.info("Register request received for username: {}", registerRequestDTO.getUsername());

        String url = userServiceUrl + "/auth/register";

        ResponseEntity<Object> response = restTemplate.postForEntity(url, registerRequestDTO, Object.class);

        logger.info("Register response received from User Service for username: {}", registerRequestDTO.getUsername());

        return response;
    }

}