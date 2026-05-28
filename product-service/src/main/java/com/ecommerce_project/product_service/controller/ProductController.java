package com.ecommerce_project.product_service.controller;

import com.ecommerce_project.product_service.dto.product.ProductRequestDTO;
import com.ecommerce_project.product_service.dto.variant.VariantRequestDTO;
import com.ecommerce_project.product_service.entity.Category;
import com.ecommerce_project.product_service.entity.Product;
import com.ecommerce_project.product_service.entity.ProductVariant;
import com.ecommerce_project.product_service.repository.CategoryRepository;
import com.ecommerce_project.product_service.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @PostMapping
    public Product createProduct(@RequestBody ProductRequestDTO request) {
        Category category = categoryRepository.findById(request.getCategoryId()).get();

        Product product = new Product();
        product.setCategory(category);
        product.setBrandId(request.getBrandId());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setMainImageKey(request.getMainImageKey());
        product.setStatus(request.getStatus());

        if (request.getVariants() != null) {
            List<ProductVariant> variants = new ArrayList<>();
            for (VariantRequestDTO v : request.getVariants()) {
                ProductVariant variant = new ProductVariant();
                variant.setProduct(product);
                variant.setSku(v.getSku());
                variant.setColor(v.getColor());
                variant.setSize(v.getSize());
                variant.setPrice(v.getPrice());
                variant.setIsActive(v.getIsActive());
                variants.add(variant);
            }
            product.setVariants(variants);
        }

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
        return productRepository.save(product);
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return "Product deleted successfully";
    }
}