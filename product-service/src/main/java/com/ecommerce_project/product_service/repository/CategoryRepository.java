package com.ecommerce_project.product_service.repository;

import com.ecommerce_project.product_service.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Check if a slug already exists in DB
    // Used in create - to make sure slug is unique
    boolean existsBySlug(String slug);

    // Check if a slug exists but belongs to a different category
    // Used in update - same slug on same category is fine, but not on another category
    boolean existsBySlugAndCategoryIdNot(String slug, Long categoryId);
}

//jpaRepository provides basic CRUD operations.
/* save()       ← create/update
findById()   ← get by id
findAll()    ← get all
deleteById() ← delete  */