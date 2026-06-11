package com.ecommerce_project.user_service.service;

import com.ecommerce_project.user_service.dto.LoginRequest;
import com.ecommerce_project.user_service.dto.LoginResponse;
import com.ecommerce_project.user_service.dto.RegisterRequest;
import com.ecommerce_project.user_service.dto.RegisterResponse;
import com.ecommerce_project.user_service.entity.User;
import com.ecommerce_project.user_service.repository.UserRepository;
import com.ecommerce_project.user_service.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class); //Creates a logger instance for this class, which can be used to log messages for debugging and monitoring purposes

    @Autowired
    private UserRepository userRepository; //Repository interface for performing CRUD operations on the User entity. It provides methods to find users by email and phone, as well as standard JPA repository methods for saving and retrieving users from the database.

    @Autowired
    private PasswordEncoder passwordEncoder; //Used to hash passwords before storing them in the database and to verify passwords during login. The PasswordEncoder bean will be configured elsewhere in the application (e.g., using BCryptPasswordEncoder) to provide secure password hashing.

    @Autowired
    private JwtUtil jwtUtil; //Utility class for generating JSON Web Tokens (JWTs) for authenticated users. It will be used in the login method to create a token that can be returned to the client and used for subsequent authenticated requests.

    public RegisterResponse register(RegisterRequest request) {

        logger.info("Register attempt for email: {}", request.getEmail());

        User existingUser = userRepository.findByEmail(request.getEmail());

        if (existingUser != null) {
            logger.warn("Email already registered: {}", request.getEmail());
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole("customer");
        user.setIsActive(true);
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());
        user.setCreatedBy("system");
        user.setModifiedBy("system");

        userRepository.save(user);

        logger.info("User registered successfully: {}", request.getEmail());

        return new RegisterResponse("User registered successfully");
    }

    public LoginResponse login(LoginRequest request) {

        logger.info("Login attempt for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail());


        // Check if user exists
        if (user == null) {
            logger.warn("User not found: {}", request.getEmail());
            throw new RuntimeException("User not found");
        }

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            logger.warn("Invalid password for: {}", request.getEmail());
            throw new RuntimeException("Invalid password");
        }


        // Check if account is active
        if (!user.getIsActive()) {
            logger.warn("Account disabled: {}", request.getEmail());
            throw new RuntimeException("Account is disabled");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());//Generates a JWT token for the authenticated user using the JwtUtil class. The token will include the user's email and role as claims, which can be used for authorization in subsequent requests. This token is then returned to the client in the LoginResponse, allowing the client to include it in the Authorization header of future requests to access protected resources.

        logger.info("Login successful for: {}", request.getEmail());

        return new LoginResponse(token, user.getRole());
    }
}