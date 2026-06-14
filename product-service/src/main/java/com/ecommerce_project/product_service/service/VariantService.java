package com.ecommerce_project.product_service.service;

import com.ecommerce_project.product_service.dto.variant.VariantRequestDTO;
import com.ecommerce_project.product_service.dto.variant.VariantResponseDTO;
import com.ecommerce_project.product_service.entity.Product;
import com.ecommerce_project.product_service.entity.ProductVariant;
import com.ecommerce_project.product_service.exception.ResourceNotFoundException;
import com.ecommerce_project.product_service.repository.ProductRepository;
import com.ecommerce_project.product_service.repository.ProductVariantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VariantService {

    // Logger for this class
    private static final Logger logger = LoggerFactory.getLogger(VariantService.class);

    @Autowired
    private ProductVariantRepository variantRepository;

    @Autowired
    private ProductRepository productRepository;

    // ===================== CREATE =====================
    public VariantResponseDTO createVariant(VariantRequestDTO request, String username) {

        logger.info("Creating variant with sku: {} by user: {}", request.getSku(), username);

        // Check if product exists
        if (!productRepository.existsById(request.getProductId())) {
            logger.error("Product not found with id: {}", request.getProductId());
            throw new ResourceNotFoundException("Product not found with id: " + request.getProductId());
        }

        // Check if SKU already exists
        if (variantRepository.existsBySku(request.getSku())) {
            logger.warn("SKU already exists: {}", request.getSku());
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
        logger.info("Variant created successfully with id: {}", savedVariant.getVariantId());
        return mapToResponseDTO(savedVariant);
    }

    // ===================== GET ALL VARIANTS =====================
    public List<VariantResponseDTO> getAllVariants() {

        logger.info("Fetching all variants");

        List<ProductVariant> variants = variantRepository.findAll();
        List<VariantResponseDTO> responseDTOs = new ArrayList<>();

    for (ProductVariant variant : variants) {
        responseDTOs.add(mapToResponseDTO(variant));
    }

    logger.info("Total variants fetched: {}", responseDTOs.size());
        return responseDTOs;
    }

    // ===================== GET VARIANT BY ID =====================
    public VariantResponseDTO getVariantById(VariantRequestDTO request) {

        logger.info("Fetching variant with id: {}", request.getVariantId());

        if (!variantRepository.existsById(request.getVariantId())) {
            logger.error("Variant not found with id: {}", request.getVariantId());
            throw new ResourceNotFoundException("Variant not found with id: " + request.getVariantId());
        }

        ProductVariant variant = variantRepository.findById(request.getVariantId()).get();

        logger.info("Variant fetched successfully with id: {}", request.getVariantId());
            return mapToResponseDTO(variant);
    }


    // ===================== UPDATE =====================
    public VariantResponseDTO updateVariant(VariantRequestDTO request, String username) {

        logger.info("Updating variant with id: {} by user: {}", request.getVariantId(), username);

        // Check variantId is provided
        if (request.getVariantId() == null) {
            logger.warn("Variant id is missing in update request");
            throw new RuntimeException("Variant id cannot be empty");
        }

        // Check if variant exists
        if (!variantRepository.existsById(request.getVariantId())) {
            logger.error("Variant not found with id: {}", request.getVariantId());
            throw new ResourceNotFoundException("Variant not found with id: " + request.getVariantId());
        }

        // Check if SKU is taken by another variant
        if (variantRepository.existsBySkuAndVariantIdNot(request.getSku(), request.getVariantId())) {
            logger.warn("SKU already exists: {}", request.getSku());
            throw new RuntimeException("SKU already exists: " + request.getSku());
        }

        ProductVariant variant = variantRepository.findById(request.getVariantId()).get();
        variant.setSku(request.getSku());
        variant.setColor(request.getColor());
        variant.setSize(request.getSize());
        variant.setPrice(request.getPrice());
        variant.setModifiedBy(username);

        ProductVariant updatedVariant = variantRepository.save(variant);
        logger.info("Variant updated successfully with id: {}", updatedVariant.getVariantId());
        return mapToResponseDTO(updatedVariant);
    }

    // ===================== DELETE =====================
    public String deleteVariant(VariantRequestDTO request, String username) {

        logger.info("Deleting variant with id: {} by user: {}", request.getVariantId(), username);

        // Check variantId is provided
        if (request.getVariantId() == null) {
            logger.warn("Variant id is missing in delete request");
            throw new RuntimeException("Variant id cannot be empty");
        }

        // Check if variant exists
        if (!variantRepository.existsById(request.getVariantId())) {
            logger.error("Variant not found with id: {}", request.getVariantId());
            throw new ResourceNotFoundException("Variant not found with id: " + request.getVariantId());
        }

        // Soft delete - just set isActive to false
        ProductVariant variant = variantRepository.findById(request.getVariantId()).get();
        variant.setIsActive(false);
        variant.setModifiedBy(username);
        variantRepository.save(variant);

        logger.info("Variant soft deleted successfully with id: {}", request.getVariantId());
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