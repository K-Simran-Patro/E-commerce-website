package com.ecommerce_project.admin_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // ─── Skip filter for login and register ───────────
        if (path.startsWith("/auth") ||
            path.startsWith("/swagger-ui") ||
            path.startsWith("/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        // ─── Read Authorization header ────────────────────
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Missing or invalid Authorization header for path: {}", path);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
            return;
        }

        // ─── Extract token from header ────────────────────
        String token = authHeader.substring(7);

        // ─── Validate token ───────────────────────────────
        if (!jwtUtil.validateToken(token)) {
            logger.warn("Invalid or expired JWT token for path: {}", path);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
            return;
        }

        // ─── Extract username and role from token ─────────
        String username = jwtUtil.extractUsername(token);
        String role = jwtUtil.extractRole(token);

        logger.info("Authenticated user: {}, role: {}, path: {}", username, role, path);

        // ─── Check role is ADMIN ───────────────────────────
        if (!"ADMIN".equalsIgnoreCase(role)) {
            logger.warn("Access denied for user: {} with role: {}", username, role);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Admins only.");
            return;
        }

        // ─── Set authentication in Spring Security ────────
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

}
