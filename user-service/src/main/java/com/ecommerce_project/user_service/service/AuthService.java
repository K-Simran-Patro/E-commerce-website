package com.ecommerce_project.user_service.service;

import com.ecommerce_project.user_service.dto.LoginRequest;
import com.ecommerce_project.user_service.dto.LoginResponse;
import com.ecommerce_project.user_service.dto.RegisterRequest;
import com.ecommerce_project.user_service.dto.RegisterResponse;
import com.ecommerce_project.user_service.entity.User;
import com.ecommerce_project.user_service.entity.UserSession;
import com.ecommerce_project.user_service.repository.UserRepository;
import com.ecommerce_project.user_service.repository.UserSessionRepository;
import com.ecommerce_project.user_service.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.expiration}")
    private long expiration;

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

        if (user == null) {
            logger.warn("User not found: {}", request.getEmail());
            throw new RuntimeException("User not found");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            logger.warn("Invalid password for: {}", request.getEmail());
            throw new RuntimeException("Invalid password");
        }

        if (!user.getIsActive()) {
            logger.warn("Account disabled: {}", request.getEmail());
            throw new RuntimeException("Account is disabled");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole(), user.getUserId().toString());

        // save session record
        UserSession session = new UserSession();
        session.setUser(user);
        session.setToken(token);
        session.setExpiresAt(OffsetDateTime.now().plusSeconds(expiration / 1000));
        session.setIsActive(true);
        session.setCreatedAt(OffsetDateTime.now());
        session.setUpdatedAt(OffsetDateTime.now());
        session.setCreatedBy(user.getEmail());
        session.setModifiedBy(user.getEmail());

        userSessionRepository.save(session);

        logger.info("Login successful for: {}", request.getEmail());

        return new LoginResponse(token, user.getRole());
    }
}