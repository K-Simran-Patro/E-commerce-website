package com.ecommerce_project.product_service.controller;

import com.ecommerce_project.product_service.dto.variant.VariantRequestDTO;
import com.ecommerce_project.product_service.dto.variant.VariantResponseDTO;
import com.ecommerce_project.product_service.service.VariantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class VariantController {

    @Autowired
    private VariantService variantService;

    // Create new variant
    @PostMapping("/variants")
    public VariantResponseDTO createVariant(
            @Valid @RequestBody VariantRequestDTO request,
            @RequestHeader("X-User-Name") String username) {
        return variantService.createVariant(request, username);
    }

    // Get all variants by product
    @GetMapping("/products/{productId}/variants")
    public List<VariantResponseDTO> getVariantsByProduct(@PathVariable Long productId) {
        return variantService.getVariantsByProduct(productId);
    }

    // Update variant - id comes from request body
    @PutMapping("/variants")
    public VariantResponseDTO updateVariant(
            @Valid @RequestBody VariantRequestDTO request,
            @RequestHeader("X-User-Name") String username) {
        return variantService.updateVariant(request, username);
    }

    // Delete variant - id comes from request body
    @DeleteMapping("/variants")
    public String deleteVariant(
            @RequestBody VariantRequestDTO request,
            @RequestHeader("X-User-Name") String username) {
        return variantService.deleteVariant(request, username);
    }
}