package com.ecommerce_project.admin_service.controller;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;



// This controller acts as a proxy for all product-related operations (categories, products, variants) in the Admin Service. It forwards requests to the Product Service while adding the X-User-Name header to identify the logged-in admin making the request. Each endpoint logs the username of the admin performing the action for auditing purposes. The controller handles CRUD operations for categories, products, and product variants by forwarding the appropriate HTTP requests to the Product Service and returning clean responses to the client.
@RestController
@RequestMapping("/admin")
@Tag(name = "Admin Product Management", description = "Manage categories, products and variants")
public class ProductProxyController {

    private static final Logger logger = LoggerFactory.getLogger(ProductProxyController.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${product.service.url}")
    private String productServiceUrl;

    //  Get logged in admin username from token 
    private String getLoggedInUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    // Build headers with X-User-Name 
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Name", getLoggedInUsername());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    // ─── Rebuild clean response — avoids header conflicts ─
    private ResponseEntity<String> buildResponse(ResponseEntity<String> response) {
        return ResponseEntity
                .status(response.getStatusCode())
                .body(response.getBody());
    }



    //  CATEGORY ENDPOINTS


    @GetMapping("/categories") //Handles HTTP GET requests to /admin/categories. It logs the username of the logged-in admin making the request, constructs the URL for the Product Service's /api/categories endpoint, creates an HttpEntity with the necessary headers (including the X-User-Name), and makes an HTTP GET request to the Product Service to retrieve all categories. The response from the Product Service is then passed to the buildResponse method to create a clean response that is returned to the client.
    @Operation(summary = "Get all categories", description = "Returns list of all categories from Product Service") //Handles HTTP GET requests to /admin/categories. It logs the username of the logged-in admin making the request, constructs the URL for the Product Service's /api/categories endpoint, creates an HttpEntity with the necessary headers (including the X-User-Name), and makes an HTTP GET request to the Product Service to retrieve all categories. The response from the Product Service is then passed to the buildResponse method to create a clean response that is returned to the client.
    public ResponseEntity<String> getAllCategories() {
        logger.info("Get all categories requested by: {}", getLoggedInUsername()); //Logs the username of the logged-in admin who is making the request to retrieve all categories. This information is useful for auditing and debugging purposes, as it allows us to track which admin is performing which actions in the system.
        String url = productServiceUrl + "/api/categories"; //Constructs the URL for the Product Service's /api/categories endpoint by appending the path to the base URL of the Product Service, which is injected from the application properties. This URL will be used to make the HTTP request to retrieve all categories from the Product Service.
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders()); //Creates an HttpEntity object with the headers that include the X-User-Name of the logged-in admin. This entity is used to make the HTTP request to the Product Service, allowing the Product Service to identify which admin is making the request based on the username provided in the header.
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class); //Makes an HTTP GET request to the Product Service's /api/categories endpoint, including the X-User-Name header with the logged-in admin's username. The response from the Product Service is captured in a ResponseEntity<String> object, which contains the status code and body of the response. This response is then passed to the buildResponse method to create a clean response that is returned to the client.
        return buildResponse(response);
    }

    // GET CATEGORY BY ID
    @GetMapping("/categories/single")
    @Operation(summary = "Get category by ID", description = "Forwards get category by id request to Product Service")
    public ResponseEntity<String> getCategoryById(@RequestBody Object requestBody) {
        logger.info("Get category by id requested by: {}", getLoggedInUsername());
         String url = productServiceUrl + "/api/categories/single";
         HttpEntity<Object> entity = new HttpEntity<>(requestBody, createHeaders());
         ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
         return buildResponse(response);
    }

    @PostMapping("/categories")
    @Operation(summary = "Create category", description = "Forwards create category request to Product Service")
    public ResponseEntity<String> createCategory(@RequestBody Object requestBody) {
        logger.info("Create category requested by: {}", getLoggedInUsername());
        String url = productServiceUrl + "/api/categories";
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, createHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        return buildResponse(response);
    }

    @PutMapping("/categories")
    @Operation(summary = "Update category", description = "Forwards update category request to Product Service")
    public ResponseEntity<String> updateCategory(@RequestBody Object requestBody) {
        logger.info("Update category requested by: {}", getLoggedInUsername());
        String url = productServiceUrl + "/api/categories";
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, createHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
        return buildResponse(response);
    }

    @DeleteMapping("/categories")
    @Operation(summary = "Delete category", description = "Forwards delete category request to Product Service")
    public ResponseEntity<String> deleteCategory(@RequestBody Object requestBody) {
        logger.info("Delete category requested by: {}", getLoggedInUsername());
        String url = productServiceUrl + "/api/categories";
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, createHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
        return buildResponse(response);
    }


    
    //  PRODUCT ENDPOINTS
   

    @GetMapping("/products")
    @Operation(summary = "Get all products", description = "Returns list of all products from Product Service")
    public ResponseEntity<String> getAllProducts() {
        logger.info("Get all products requested by: {}", getLoggedInUsername());
        String url = productServiceUrl + "/api/products";
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return buildResponse(response);
    }

    @GetMapping("/products/single")
    @Operation(summary = "Get product by ID", description = "Forwards get product by id request to Product Service")
    public ResponseEntity<String> getProductById(@RequestBody Object requestBody) {
        logger.info("Get product by id requested by: {}", getLoggedInUsername());
         String url = productServiceUrl + "/api/products/single";
         HttpEntity<Object> entity = new HttpEntity<>(requestBody, createHeaders());
         ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
         return buildResponse(response);
    }

    @PostMapping("/products")
    @Operation(summary = "Create product", description = "Forwards create product request to Product Service")
    public ResponseEntity<String> createProduct(@RequestBody Object requestBody) {
        logger.info("Create product requested by: {}", getLoggedInUsername());
        String url = productServiceUrl + "/api/products";
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, createHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        return buildResponse(response);
    }

    @PutMapping("/products")
    @Operation(summary = "Update product", description = "Forwards update product request to Product Service")
    public ResponseEntity<String> updateProduct(@RequestBody Object requestBody) {
        logger.info("Update product requested by: {}", getLoggedInUsername());
        String url = productServiceUrl + "/api/products";
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, createHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
        return buildResponse(response);
    }

    @DeleteMapping("/products")
    @Operation(summary = "Delete product", description = "Forwards delete product request to Product Service")
    public ResponseEntity<String> deleteProduct(@RequestBody Object requestBody) {
        logger.info("Delete product requested by: {}", getLoggedInUsername());
        String url = productServiceUrl + "/api/products";
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, createHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
        return buildResponse(response);
    }


    
    //  PRODUCT VARIANT ENDPOINTS
  

    @GetMapping("/variants")
    @Operation(summary = "Get all variants", description = "Returns list of all product variants from Product Service")
    public ResponseEntity<String> getAllVariants() {
        logger.info("Get all variants requested by: {}", getLoggedInUsername());
        String url = productServiceUrl + "/api/variants";
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return buildResponse(response);
    }


    // GET VARIANT BY ID
    @GetMapping("/variants/single")
    @Operation(summary = "Get variant by ID", description = "Forwards get variant by id request to Product Service")
    public ResponseEntity<String> getVariantById(@RequestBody Object requestBody) {
         logger.info("Get variant by id requested by: {}", getLoggedInUsername());
         String url = productServiceUrl + "/api/variants/single";
         HttpEntity<Object> entity = new HttpEntity<>(requestBody, createHeaders());
         ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
         return buildResponse(response);
    }

    @PostMapping("/variants")
    @Operation(summary = "Create variant", description = "Forwards create variant request to Product Service")
    public ResponseEntity<String> createVariant(@RequestBody Object requestBody) {
        logger.info("Create variant requested by: {}", getLoggedInUsername());
        String url = productServiceUrl + "/api/variants";
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, createHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        return buildResponse(response);
    }

    @PutMapping("/variants")
    @Operation(summary = "Update variant", description = "Forwards update variant request to Product Service")
    public ResponseEntity<String> updateVariant(@RequestBody Object requestBody) {
        logger.info("Update variant requested by: {}", getLoggedInUsername());
        String url = productServiceUrl + "/api/variants";
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, createHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
        return buildResponse(response);
    }

    @DeleteMapping("/variants")
    @Operation(summary = "Delete variant", description = "Forwards delete variant request to Product Service")
    public ResponseEntity<String> deleteVariant(@RequestBody Object requestBody) {
        logger.info("Delete variant requested by: {}", getLoggedInUsername());
        String url = productServiceUrl + "/api/variants";
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, createHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
        return buildResponse(response);
    }

}