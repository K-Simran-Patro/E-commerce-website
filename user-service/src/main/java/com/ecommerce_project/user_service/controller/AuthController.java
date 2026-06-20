package com.ecommerce_project.user_service.controller;

import com.ecommerce_project.user_service.dto.LoginRequest;
import com.ecommerce_project.user_service.dto.LoginResponse;
import com.ecommerce_project.user_service.dto.RegisterRequest;
import com.ecommerce_project.user_service.dto.RegisterResponse;
import com.ecommerce_project.user_service.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("Register request received for: {}", request.getEmail());
        RegisterResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request,
                                                HttpServletRequest httpRequest) {
        logger.info("Login request received for: {}", request.getEmail());

        String deviceInfo = httpRequest.getHeader("User-Agent");
        String ipAddress = httpRequest.getRemoteAddr();

        LoginResponse response = authService.login(request, deviceInfo, ipAddress);
        return ResponseEntity.ok(response);
    }
}