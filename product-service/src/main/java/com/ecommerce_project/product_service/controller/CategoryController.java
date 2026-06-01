package com.ecommerce_project.product_service.controller;

import com.ecommerce_project.product_service.dto.category.CategoryRequestDTO;
import com.ecommerce_project.product_service.dto.category.CategoryResponseDTO;
import com.ecommerce_project.product_service.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Create new category
    @PostMapping
    public CategoryResponseDTO createCategory(
            @RequestBody CategoryRequestDTO request,
            @RequestHeader("X-User-Name") String username) {
        return categoryService.createCategory(request, username);
    }

    // Get all categories
    @GetMapping
    public List<CategoryResponseDTO> getAllCategories() {
        return categoryService.getAllCategories();
    }

    // Get category by id
    @GetMapping("/{id}")
    public CategoryResponseDTO getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    // Update category - id comes from request body
    @PutMapping
    public CategoryResponseDTO updateCategory(
            @RequestBody CategoryRequestDTO request,
            @RequestHeader("X-User-Name") String username) {
        return categoryService.updateCategory(request, username);
    }

    // Delete category - id comes from request body
    @DeleteMapping
    public String deleteCategory(
            @RequestBody CategoryRequestDTO request,
            @RequestHeader("X-User-Name") String username) {
        return categoryService.deleteCategory(request, username);
    }
}