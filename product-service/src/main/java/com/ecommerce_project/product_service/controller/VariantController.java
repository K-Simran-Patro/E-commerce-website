package com.ecommerce_project.product_service.controller;

import com.ecommerce_project.product_service.dto.variant.VariantRequestDTO;
import com.ecommerce_project.product_service.entity.Product;
import com.ecommerce_project.product_service.entity.ProductVariant;
import com.ecommerce_project.product_service.repository.ProductRepository;
import com.ecommerce_project.product_service.repository.ProductVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api")
public class VariantController {

    @Autowired
    private ProductVariantRepository variantRepository;

    @Autowired
    private ProductRepository productRepository;

    @PostMapping("/products/{productId}/variants")
    public ProductVariant addVariant(@PathVariable Long productId, @RequestBody VariantRequestDTO request) {
        Product product = productRepository.findById(productId).get();

        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        variant.setSku(request.getSku());
        variant.setColor(request.getColor());
        variant.setSize(request.getSize());
        variant.setPrice(request.getPrice());
        variant.setIsActive(request.getIsActive());
        variant.setCreatedBy("admin");
        variant.setModifiedBy("admin");

        return variantRepository.save(variant);
    }

    @GetMapping("/products/{productId}/variants")
    public List<ProductVariant> getVariantsByProduct(@PathVariable Long productId) {
        Product product = productRepository.findById(productId).get();
        return product.getVariants();
    }

    @PutMapping("/variants/{variantId}")
    public ProductVariant updateVariant(@PathVariable Long variantId, @RequestBody VariantRequestDTO request) {
        ProductVariant variant = variantRepository.findById(variantId).get();
        variant.setSku(request.getSku());
        variant.setColor(request.getColor());
        variant.setSize(request.getSize());
        variant.setPrice(request.getPrice());
        variant.setIsActive(request.getIsActive());
        return variantRepository.save(variant);
    }

    @DeleteMapping("/variants/{variantId}")
    public String deleteVariant(@PathVariable Long variantId) {
        variantRepository.deleteById(variantId);
        return "Variant deleted successfully";
    }
}