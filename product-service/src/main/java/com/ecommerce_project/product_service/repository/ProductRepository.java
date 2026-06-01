package com.ecommerce_project.product_service.repository;

import com.ecommerce_project.product_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Check if a product name already exists under the same category
    // Used in create - to avoid duplicate product names in same category
    boolean existsByNameAndCategoryId(String name, Long categoryId);

    // Check if a product name exists under same category but belongs to different product
    // Used in update - same name on same product is fine, but not on another product in same category
    boolean existsByNameAndCategoryIdAndProductIdNot(String name, Long categoryId, Long productId);
}