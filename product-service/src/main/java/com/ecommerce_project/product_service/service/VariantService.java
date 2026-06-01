package com.ecommerce_project.product_service.service;

import com.ecommerce_project.product_service.dto.variant.VariantRequestDTO;
import com.ecommerce_project.product_service.dto.variant.VariantResponseDTO;
import com.ecommerce_project.product_service.entity.Product;
import com.ecommerce_project.product_service.entity.ProductVariant;
import com.ecommerce_project.product_service.exception.ResourceNotFoundException;
import com.ecommerce_project.product_service.repository.ProductRepository;
import com.ecommerce_project.product_service.repository.ProductVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VariantService {

    @Autowired
    private ProductVariantRepository variantRepository;

    @Autowired
    private ProductRepository productRepository;

    // ===================== CREATE =====================
    public VariantResponseDTO createVariant(VariantRequestDTO request, String username) {

        // Check productId is provided
        if (request.getProductId() == null) {
            throw new RuntimeException("Product id cannot be empty");
        }

        // Check SKU is not empty
        if (request.getSku() == null || request.getSku().trim().isEmpty()) {
            throw new RuntimeException("SKU cannot be empty");
        }

        // Check price is not null or negative
        if (request.getPrice() == null) {
            throw new RuntimeException("Price cannot be empty");
        }
        if (request.getPrice().doubleValue() < 0) {
            throw new RuntimeException("Price cannot be negative");
        }

        // Check if product exists
        if (!productRepository.existsById(request.getProductId())) {
            throw new ResourceNotFoundException("Product not found with id: " + request.getProductId());
        }

        // Check if SKU already exists
        if (variantRepository.existsBySku(request.getSku())) {
            throw new RuntimeException("SKU already exists: " + request.getSku());
        }

        Product product = productRepository.findById(request.getProductId()).get();

        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        variant.setSku(request.getSku());
        variant.setColor(request.getColor());
        variant.setSize(request.getSize());
        variant.setPrice(request.getPrice());
        variant.setIsActive(true);
        variant.setCreatedBy(username);
        variant.setModifiedBy(username);

        ProductVariant savedVariant = variantRepository.save(variant);
        return mapToResponseDTO(savedVariant);
    }

    // ===================== GET BY PRODUCT =====================
    public List<VariantResponseDTO> getVariantsByProduct(Long productId) {

        // Check if product exists
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }

        Product product = productRepository.findById(productId).get();
        List<ProductVariant> variants = product.getVariants();
        List<VariantResponseDTO> responseDTOs = new ArrayList<>();

        for (ProductVariant variant : variants) {
            responseDTOs.add(mapToResponseDTO(variant));
        }

        return responseDTOs;
    }

    // ===================== UPDATE =====================
    public VariantResponseDTO updateVariant(VariantRequestDTO request, String username) {

        // Check variantId is provided
        if (request.getVariantId() == null) {
            throw new RuntimeException("Variant id cannot be empty");
        }

        // Check SKU is not empty
        if (request.getSku() == null || request.getSku().trim().isEmpty()) {
            throw new RuntimeException("SKU cannot be empty");
        }

        // Check price is not null or negative
        if (request.getPrice() == null) {
            throw new RuntimeException("Price cannot be empty");
        }
        if (request.getPrice().doubleValue() < 0) {
            throw new RuntimeException("Price cannot be negative");
        }

        // Check if variant exists
        if (!variantRepository.existsById(request.getVariantId())) {
            throw new ResourceNotFoundException("Variant not found with id: " + request.getVariantId());
        }

        // Check if SKU is taken by another variant
        if (variantRepository.existsBySkuAndVariantIdNot(request.getSku(), request.getVariantId())) {
            throw new RuntimeException("SKU already exists: " + request.getSku());
        }

        ProductVariant variant = variantRepository.findById(request.getVariantId()).get();
        variant.setSku(request.getSku());
        variant.setColor(request.getColor());
        variant.setSize(request.getSize());
        variant.setPrice(request.getPrice());
        variant.setModifiedBy(username);

        ProductVariant updatedVariant = variantRepository.save(variant);
        return mapToResponseDTO(updatedVariant);
    }

    // ===================== DELETE =====================
    public String deleteVariant(VariantRequestDTO request, String username) {

        // Check variantId is provided
        if (request.getVariantId() == null) {
            throw new RuntimeException("Variant id cannot be empty");
        }

        // Check if variant exists
        if (!variantRepository.existsById(request.getVariantId())) {
            throw new ResourceNotFoundException("Variant not found with id: " + request.getVariantId());
        }

        // Soft delete - just set isActive to false
        ProductVariant variant = variantRepository.findById(request.getVariantId()).get();
        variant.setIsActive(false);
        variant.setModifiedBy(username);
        variantRepository.save(variant);

        return "Variant deleted successfully";
    }

    // ===================== HELPER - Map Entity to ResponseDTO =====================
    private VariantResponseDTO mapToResponseDTO(ProductVariant variant) {

        VariantResponseDTO responseDTO = new VariantResponseDTO();
        responseDTO.setVariantId(variant.getVariantId());
        responseDTO.setProductId(variant.getProduct().getProductId());
        responseDTO.setSku(variant.getSku());
        responseDTO.setColor(variant.getColor());
        responseDTO.setSize(variant.getSize());
        responseDTO.setPrice(variant.getPrice());
        responseDTO.setIsActive(variant.getIsActive());

        return responseDTO;
    }
}