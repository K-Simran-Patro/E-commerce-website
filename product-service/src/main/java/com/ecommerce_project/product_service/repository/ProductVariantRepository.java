package com.ecommerce_project.product_service.repository;

import com.ecommerce_project.product_service.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    // Check if a SKU already exists across all products
    // Used in create - to make sure SKU is globally unique
    boolean existsBySku(String sku);

    // Check if a SKU exists but belongs to a different variant
    // Used in update - same SKU on same variant is fine, but not on another variant
    boolean existsBySkuAndVariantIdNot(String sku, Long variantId);
}