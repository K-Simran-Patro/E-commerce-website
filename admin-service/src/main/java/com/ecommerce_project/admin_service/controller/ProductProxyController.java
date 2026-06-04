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

    // ─── Get logged in admin username from token ──────────
    private String getLoggedInUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    // ─── Build headers with X-User-Name ──────────────────
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
    @Operation(summary = "Get all categories", description = "Returns list of all categories from Product Service")
    public ResponseEntity<Object> getAllCategories() {
        logger.info("Get all categories requested by: {}", getLoggedInUsername());
        String url = productServiceUrl + "/api/categories";
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        return restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
    }

    @PostMapping("/categories")
    @Operation(summary = "Create category", description = "Forwards create category request to Product Service")
    public ResponseEntity<Object> createCategory(@RequestBody Object requestBody) {
        logger.info("Create category requested by: {}", getLoggedInUsername());
        String url = productServiceUrl + "/api/categories";
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, createHeaders());
        return restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
    }

    @PutMapping("/categories")
    @Operation(summary = "Update category", description = "Forwards update category request to Product Service")
    public ResponseEntity<Object> updateCategory(@RequestBody Object requestBody) {
        logger.info("Update category requested by: {}", getLoggedInUsername());
        String url = productServiceUrl + "/api/categories";
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, createHeaders());
        return restTemplate.exchange(url, HttpMethod.PUT, entity, Object.class);
    }

    @DeleteMapping("/categories")
    @Operation(summary = "Delete category", description = "Forwards delete category request to Product Service")
    public ResponseEntity<Object> deleteCategory(@RequestBody Object requestBody) {
        logger.info("Delete category requested by: {}", getLoggedInUsername());
        String url = productServiceUrl + "/api/categories";
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, createHeaders());
        return restTemplate.exchange(url, HttpMethod.DELETE, entity, Object.class);
    }


    // ════════════════════════════════════════════════════
    //  PRODUCT ENDPOINTS
    // ════════════════════════════════════════════════════

    @GetMapping("/products")
    @Operation(summary = "Get all products", description = "Returns list of all products from Product Service")
    public ResponseEntity<Object> getAllProducts() {
        logger.info("Get all products requested by: {}", getLoggedInUsername());
        String url = productServiceUrl + "/api/products";
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        return restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
    }

    @PostMapping("/products")
    @Operation(summary = "Create product", description = "Forwards create product request to Product Service")
    public ResponseEntity<Object> createProduct(@RequestBody Object requestBody) {
        logger.info("Create product requested by: {}", getLoggedInUsername());
        String url = productServiceUrl + "/api/products";
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, createHeaders());
        return restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
    }

    @PutMapping("/products")
    @Operation(summary = "Update product", description = "Forwards update product request to Product Service")
    public ResponseEntity<Object> updateProduct(@RequestBody Object requestBody) {
        logger.info("Update product requested by: {}", getLoggedInUsername());
        String url = productServiceUrl + "/api/products";
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, createHeaders());
        return restTemplate.exchange(url, HttpMethod.PUT, entity, Object.class);
    }

    @DeleteMapping("/products")
    @Operation(summary = "Delete product", description = "Forwards delete product request to Product Service")
    public ResponseEntity<Object> deleteProduct(@RequestBody Object requestBody) {
        logger.info("Delete product requested by: {}", getLoggedInUsername());
        String url = productServiceUrl + "/api/products";
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, createHeaders());
        return restTemplate.exchange(url, HttpMethod.DELETE, entity, Object.class);
    }


    // ════════════════════════════════════════════════════
    //  PRODUCT VARIANT ENDPOINTS
    // ════════════════════════════════════════════════════

    @GetMapping("/variants")
    @Operation(summary = "Get all variants", description = "Returns list of all product variants from Product Service")
    public ResponseEntity<Object> getAllVariants() {
        logger.info("Get all variants requested by: {}", getLoggedInUsername());
        String url = productServiceUrl + "/api/variants";
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        return restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
    }

    @PostMapping("/variants")
    @Operation(summary = "Create variant", description = "Forwards create variant request to Product Service")
    public ResponseEntity<Object> createVariant(@RequestBody Object requestBody) {
        logger.info("Create variant requested by: {}", getLoggedInUsername());
        String url = productServiceUrl + "/api/variants";
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, createHeaders());
        return restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
    }

    @PutMapping("/variants")
    @Operation(summary = "Update variant", description = "Forwards update variant request to Product Service")
    public ResponseEntity<Object> updateVariant(@RequestBody Object requestBody) {
        logger.info("Update variant requested by: {}", getLoggedInUsername());
        String url = productServiceUrl + "/api/variants";
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, createHeaders());
        return restTemplate.exchange(url, HttpMethod.PUT, entity, Object.class);
    }

    @DeleteMapping("/variants")
    @Operation(summary = "Delete variant", description = "Forwards delete variant request to Product Service")
    public ResponseEntity<Object> deleteVariant(@RequestBody Object requestBody) {
        logger.info("Delete variant requested by: {}", getLoggedInUsername());
        String url = productServiceUrl + "/api/variants";
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, createHeaders());
        return restTemplate.exchange(url, HttpMethod.DELETE, entity, Object.class);
    }

}