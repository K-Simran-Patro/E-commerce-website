package com.ecommerce_project.admin_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin Product Management", description = "Manage categories, products and variants")
public class ProductProxyController {

    private static final Logger logger = LoggerFactory.getLogger(ProductProxyController.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${product.service.url}")
    private String productServiceUrl;

    private String getLoggedInUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Name", getLoggedInUsername());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    // ════════════════════════════════════════════════════
    //  CATEGORY ENDPOINTS
    // ════════════════════════════════════════════════════

    @GetMapping("/categories")
    @Operation(summary = "Get all categories")
    public ResponseEntity<String> getAllCategories() {
        logger.info("Get all categories requested by: {}", getLoggedInUsername());
        String url = productServiceUrl + "/api/categories";
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }

    @PostMapping("/categories")
    @Operation(summary = "Create category")
    public ResponseEntity<String> createCategory(@RequestBody Object requestBody) {
        logger.info("Create category requested by: {}", getLoggedInUsername());
        String url = productServiceUrl + "/api/categories";
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, createHeaders());
        return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }

    @PutMapping("/categories")
    @Operation(summary = "Update category")
    public ResponseEntity<String> updateCategory(@RequestBody Object requestBody) {
        logger.info("Update category requested by: {}", getLoggedInUsername());
        String url = productServiceUrl + "/api/categories";
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, createHeaders());
        return restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
    }

    @DeleteMapping("/categories")
    @Operation(summary = "Delete category")
    public ResponseEntity<String> deleteCategory(@RequestBody Object requestBody) {
        logger.info("Delete category requested by: {}", getLoggedInUsername());
        String url = productServiceUrl + "/api/categories";
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, createHeaders());
        return restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
    }

    // ════════════════════════════════════════════════════
    //  PRODUCT ENDPOINTS
    // ════════════════════════════════════════════════════

    @GetMapping("/products")
    @Operation(summary = "Get all products")
    public ResponseEntity<String> getAllProducts() {
        logger.info("Get all products requested by: {}", getLoggedInUsername());
        String url = productServiceUrl + "/api/products";
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }

    @PostMapping("/products")
    @Operation(summary = "Create product")
    public ResponseEntity<String> createProduct(@RequestBody Object requestBody) {
        logger.info("Create product requested by: {}", getLoggedInUsername());
        String url = productServiceUrl + "/api/products";
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, createHeaders());
        return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }

    @PutMapping("/products")
    @Operation(summary = "Update product")
    public ResponseEntity<String> updateProduct(@RequestBody Object requestBody) {
        logger.info("Update product requested by: {}", getLoggedInUsername());
        String url = productServiceUrl + "/api/products";
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, createHeaders());
        return restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
    }

    @DeleteMapping("/products")
    @Operation(summary = "Delete product")
    public ResponseEntity<String> deleteProduct(@RequestBody Object requestBody) {
        logger.info("Delete product requested by: {}", getLoggedInUsername());
        String url = productServiceUrl + "/api/products";
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, createHeaders());
        return restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
    }

    // ════════════════════════════════════════════════════
    //  PRODUCT VARIANT ENDPOINTS
    // ════════════════════════════════════════════════════

    @GetMapping("/variants")
    @Operation(summary = "Get all variants")
    public ResponseEntity<String> getAllVariants() {
        logger.info("Get all variants requested by: {}", getLoggedInUsername());
        String url = productServiceUrl + "/api/variants";
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }

    @PostMapping("/variants")
    @Operation(summary = "Create variant")
    public ResponseEntity<String> createVariant(@RequestBody Object requestBody) {
        logger.info("Create variant requested by: {}", getLoggedInUsername());
        String url = productServiceUrl + "/api/variants";
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, createHeaders());
        return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }

    @PutMapping("/variants")
    @Operation(summary = "Update variant")
    public ResponseEntity<String> updateVariant(@RequestBody Object requestBody) {
        logger.info("Update variant requested by: {}", getLoggedInUsername());
        String url = productServiceUrl + "/api/variants";
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, createHeaders());
        return restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
    }

    @DeleteMapping("/variants")
    @Operation(summary = "Delete variant")
    public ResponseEntity<String> deleteVariant(@RequestBody Object requestBody) {
        logger.info("Delete variant requested by: {}", getLoggedInUsername());
        String url = productServiceUrl + "/api/variants";
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, createHeaders());
        return restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
    }

}