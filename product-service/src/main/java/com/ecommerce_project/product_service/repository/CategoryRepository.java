package com.ecommerce_project.product_service.repository;

import com.ecommerce_project.product_service.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

}


//jpaRepository provides basic CRUD operations.
/* save()       ← create/update
findById()   ← get by id
findAll()    ← get all
deleteById() ← delete  */