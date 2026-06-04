package com.ecommerce_project.admin_service.config;

import com.ecommerce_project.admin_service.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // ─── Disable CSRF ──────────────────────────────────
            .csrf(csrf -> csrf.disable())

            // ─── Define which routes are open and which are protected ──
            .authorizeHttpRequests(auth -> auth
                // Login and register are open — no token needed
                .requestMatchers("/auth/**").permitAll()
                // Swagger is open — so frontend dev can read docs
                .requestMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/api-docs/**"
                ).permitAll()
                // Everything else requires ADMIN role
                .anyRequest().hasRole("ADMIN")
            )

            // ─── No sessions — we use JWT, not cookies ─────────
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // ─── Add our JwtFilter before Spring's default filter ──
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}