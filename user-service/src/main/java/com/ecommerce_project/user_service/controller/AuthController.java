package com.ecommerce_project.user_service.controller;

import com.ecommerce_project.user_service.dto.LoginRequest;
import com.ecommerce_project.user_service.dto.LoginResponse;
import com.ecommerce_project.user_service.dto.RegisterRequest;
import com.ecommerce_project.user_service.dto.RegisterResponse;
import com.ecommerce_project.user_service.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController //Marks this class as a Spring REST controller that can handle HTTP requests and return JSON responses
@RequestMapping("/auth") //Base URL for all endpoints in this controller will start with /auth (e.g., /auth/register, /auth/login)
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class); //Creates a logger instance for this class, which can be used to log messages for debugging and monitoring purposes

    @Autowired //Tells Spring to automatically inject an instance of AuthService into this controller, so we can use it to perform authentication-related business logic (like registering users and handling logins)
    private AuthService authService; //Service layer that contains the business logic for authentication (like registering users and handling logins). The controller will call methods on this service to perform the actual work of registering and logging in users.

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) //Handles HTTP POST requests to /auth/register. It takes a RegisterRequest object from the request body (which contains the data needed to register a new user) and returns a ResponseEntity containing a RegisterResponse object, which includes the details of the newly registered user. The @Valid annotation ensures that the incoming request data is validated according to the constraints defined in the RegisterRequest class.
    {
        logger.info("Register request received for: {}", request.getEmail()); //Logs an informational message indicating that a registration request has been received, along with the email of the user trying to register. This can help in monitoring and debugging registration attempts.
        RegisterResponse response = authService.register(request); //Calls the register method of the AuthService, passing in the registration request data. The AuthService will handle the actual logic of creating a new user account and return a RegisterResponse object with the details of the registered user.
        return ResponseEntity.status(HttpStatus.CREATED).body(response); //Returns an HTTP response with a status code of 201 Created, along with the RegisterResponse object in the response body. This indicates that the user was successfully registered and provides the details of the new user account.
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        logger.info("Login request received for: {}", request.getEmail());
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}