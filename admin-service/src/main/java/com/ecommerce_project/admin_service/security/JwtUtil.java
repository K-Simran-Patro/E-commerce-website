package com.ecommerce_project.admin_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;


// This class provides utility methods for working with JWT tokens, such as extracting the username and role from the token, and validating the token's expiration. It uses the secret key defined in the application properties to parse and validate the JWT tokens received in requests.


@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String secret;

    // ─── Get the signing key from secret ──────────────────
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // ─── Extract all data (claims) from token ─────────────
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ─── Extract username from token ──────────────────────
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // ─── Extract role from token ──────────────────────────
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    // ─── Check if token is expired ────────────────────────
    private boolean isTokenExpired(String token) {
        Date expiry = extractAllClaims(token).getExpiration();
        return expiry.before(new Date());
    }

    // ─── Check if token is valid ──────────────────────────
    public boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

}