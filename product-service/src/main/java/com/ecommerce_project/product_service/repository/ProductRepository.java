package com.ecommerce_project.product_service.repository;

import com.ecommerce_project.product_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Check if a product name already exists within the same category
    // Used in create - to make sure name is unique within category
    boolean existsByNameAndCategoryCategoryId(String name, Long categoryId);

    // Check if a product name exists within the same category but belongs to a different product
    // Used in update - same name on same product is fine, but not on another product
    boolean existsByNameAndCategoryCategoryIdAndProductIdNot(String name, Long categoryId, Long productId);
}