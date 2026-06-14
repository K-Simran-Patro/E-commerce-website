package com.ecommerce_project.product_service.controller;

import com.ecommerce_project.product_service.dto.category.CategoryRequestDTO;
import com.ecommerce_project.product_service.dto.category.CategoryResponseDTO;
import com.ecommerce_project.product_service.service.CategoryService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController //Marks this class as a REST API controller that can handle HTTP requests and return JSON responses
@RequestMapping("/api/categories") //Base URL for all endpoints in this controller will start with /api/categories
public class CategoryController {

    @Autowired //Tells Spring to automatically inject an instance of CategoryService into this controller, so we can use it to perform business logic related to categories
    private CategoryService categoryService;

    // Create new category
    @PostMapping
    public CategoryResponseDTO createCategory //Handles HTTP POST requests to /api/categories. It takes a CategoryRequestDTO object from the request body (which contains the data needed to create a category) and a username from the request header (to track who is creating the category). It returns a CategoryResponseDTO object, which contains the details of the newly created category.
    ( 
            @Valid@RequestBody CategoryRequestDTO request, //@requestbody coverts JSON into java object and @valid makes sure the data in the request body meets the validation rules defined in CategoryRequestDTO (like not null, size limits, etc.)
            @RequestHeader("X-User-Name") String username) {
        return categoryService.createCategory(request, username);
    }

    // Get all categories
    @GetMapping
    public List<CategoryResponseDTO> getAllCategories() {
        return categoryService.getAllCategories();
    }

    // Get category by ID (id from request body)
    @GetMapping("/single")
    public CategoryResponseDTO getCategoryById(@RequestBody CategoryRequestDTO request) {
        return categoryService.getCategoryById(request);
    }

    // Update category - id comes from request body
    @PutMapping
    public CategoryResponseDTO updateCategory(
            @Valid@RequestBody CategoryRequestDTO request,
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