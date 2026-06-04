package com.ecommerce_project.admin_service.controller;

import com.ecommerce_project.admin_service.dto.LoginRequestDTO;
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

        logger.info("Login request received for email: {}", loginRequestDTO.getEmail());

        String url = userServiceUrl + "/auth/login";

        ResponseEntity<Object> response = restTemplate.postForEntity(url, loginRequestDTO, Object.class);

        logger.info("Login response received from User Service for email: {}", loginRequestDTO.getEmail());

        return response;
    }
}