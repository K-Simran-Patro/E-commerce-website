package com.ecommerce_project.product_service.repository;

import com.ecommerce_project.product_service.entity.Category;

import org.antlr.v4.runtime.atn.SemanticContext.AND;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository //Marks this interface as a Repository bean. Spring will create an implementation of this interface at runtime.Spring can detect it during component scanning.
public interface CategoryRepository extends JpaRepository<Category, Long> { //This repository works with Category entities,The primary key type is Long

    // Check if a slug already exists in DB
    // Used in create - to make sure slug is unique
    boolean existsBySlug(String slug); //existsbyslug is a method that Spring Data JPA will automatically implement based on the method name. It checks if a category with the given slug already exists in the database.SELECT COUNT(*)FROM categoriesWHERE slug = 'mobile-phones';  

    // Check if a slug exists but belongs to a different category
    // Used in update - same slug on same category is fine, but not on another category
    boolean existsBySlugAndCategoryIdNot(String slug, Long categoryId); //Does any category exist where slug = given slug AND categoryId != given categoryId
}

//jpaRepository provides basic CRUD operations.
/* save()       ← create/update
findById()   ← get by id
findAll()    ← get all
deleteById() ← delete  */