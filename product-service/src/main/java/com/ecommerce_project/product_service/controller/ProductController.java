package com.ecommerce_project.product_service.controller;

import com.ecommerce_project.product_service.dto.product.ProductRequestDTO;
import com.ecommerce_project.product_service.dto.product.ProductResponseDTO;
import com.ecommerce_project.product_service.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Create new product
    @PostMapping
    public ProductResponseDTO createProduct(
            @RequestBody ProductRequestDTO request,
            @RequestHeader("X-User-Name") String username) {
        return productService.createProduct(request, username);
    }

    // Get all products
    @GetMapping
    public List<ProductResponseDTO> getAllProducts() {
        return productService.getAllProducts();
    }

    // Get product by id
    @GetMapping("/{id}")
    public ProductResponseDTO getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    // Update product - id comes from request body
    @PutMapping
    public ProductResponseDTO updateProduct(
            @RequestBody ProductRequestDTO request,
            @RequestHeader("X-User-Name") String username) {
        return productService.updateProduct(request, username);
    }

    // Delete product - id comes from request body
    @DeleteMapping
    public String deleteProduct(
            @RequestBody ProductRequestDTO request,
            @RequestHeader("X-User-Name") String username) {
        return productService.deleteProduct(request, username);
    }
}