package com.ecommerce_project.product_service.repository;

import com.ecommerce_project.product_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Spring traverses Product.category.categoryId
    boolean existsByNameAndCategoryCategoryId(String name, Long categoryId);

    // Same but excludes current product (for update)
    boolean existsByNameAndCategoryCategoryIdAndProductIdNot(String name, Long categoryId, Long productId);
}