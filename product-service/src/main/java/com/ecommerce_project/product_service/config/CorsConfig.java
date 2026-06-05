package com.ecommerce_project.product_service.config;

// NOTE: Update the package name above if yours is different.
// This file goes in: src/main/java/com/ecommerce_project/product_service/config/CorsConfig.java

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    // ─── CORS Configuration ──────────────────────────────────────────────────
    // Since product-service has no Spring Security, we use WebMvcConfigurer.
    // This tells the browser which frontends are allowed to call this backend.
    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")  // apply to all routes

                // Allowed frontend origins
                // After deploying to Vercel, replace "https://your-app.vercel.app"
                // with your actual Vercel URL
                .allowedOrigins(
                        "https://e-commerce-website-eta-nine-52.vercel.app"
                )

                // Allowed HTTP methods
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")

                // Allow all headers — needed so Authorization header (JWT) is not blocked
                .allowedHeaders("*");
    }
    // ─────────────────────────────────────────────────────────────────────────

}
