package com.ecommerce_project.product_service.controller;

import com.ecommerce_project.product_service.dto.product.ProductRequestDTO;
import com.ecommerce_project.product_service.entity.Category;
import com.ecommerce_project.product_service.entity.Product;
import com.ecommerce_project.product_service.repository.CategoryRepository;
import com.ecommerce_project.product_service.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @PostMapping
    public Product createProduct(@RequestBody ProductRequestDTO request) { // Convert incoming JSON request body into a ProductRequestDTO object, which contains the data needed to create a new product. The method then uses this data to create a new Product entity, save it to the database, and return the saved product back to the client.
        Category category = categoryRepository.findById(request.getCategoryId()).get();

        Product product = new Product();
        // Set product fields using values received from the request DTO and the fetched category. The product's category is set to the category fetched from the database using the category ID provided in the request. Other fields like brand name, name, description, main image key, status, created by, and modified by are set using the corresponding values from the request DTO and hardcoded values for createdBy and modifiedBy.
        product.setCategory(category);
        product.setBrandName(request.getBrandName());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setMainImageKey(request.getMainImageKey());
        product.setStatus(request.getStatus());
        product.setCreatedBy("admin");
        product.setModifiedBy("admin");

        return productRepository.save(product);
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productRepository.findById(id).get();
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody ProductRequestDTO request) {
        Product product = productRepository.findById(id).get();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setMainImageKey(request.getMainImageKey());
        product.setStatus(request.getStatus());
        product.setBrandName(request.getBrandName());
        product.setModifiedBy("admin");
        return productRepository.save(product);
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return "Product deleted successfully";
    }
}