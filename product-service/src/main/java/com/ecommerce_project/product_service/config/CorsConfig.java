package com.ecommerce_project.product_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    // CORS Configuration 
    // This tells the browser which frontends are allowed to call this backend.
    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")  // apply to all routes

                // Allowed frontend origins
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
