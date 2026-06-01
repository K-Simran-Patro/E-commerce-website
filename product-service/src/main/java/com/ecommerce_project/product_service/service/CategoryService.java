package com.ecommerce_project.product_service.service;

import com.ecommerce_project.product_service.dto.category.CategoryRequestDTO;
import com.ecommerce_project.product_service.dto.category.CategoryResponseDTO;
import com.ecommerce_project.product_service.entity.Category;
import com.ecommerce_project.product_service.exception.ResourceNotFoundException;
import com.ecommerce_project.product_service.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // ===================== CREATE =====================
    public CategoryResponseDTO createCategory(CategoryRequestDTO request, String username) {

        // Check name is not empty
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("Category name cannot be empty");
        }

        // Check slug is not empty
        if (request.getSlug() == null || request.getSlug().trim().isEmpty()) {
            throw new RuntimeException("Category slug cannot be empty");
        }

        // Check if slug already exists
        if (categoryRepository.existsBySlug(request.getSlug())) {
            throw new RuntimeException("Slug already exists: " + request.getSlug());
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setSlug(request.getSlug());
        category.setIsActive(true);
        category.setCreatedBy(username);
        category.setModifiedBy(username);

        // If parentId is provided, fetch and set parent category
        if (request.getParentId() != null) {
            if (!categoryRepository.existsById(request.getParentId())) {
                throw new ResourceNotFoundException("Parent category not found with id: " + request.getParentId());
            }
            Category parent = categoryRepository.findById(request.getParentId()).get();
            category.setParent(parent);
        }

        Category savedCategory = categoryRepository.save(category);
        return mapToResponseDTO(savedCategory);
    }

    // ===================== GET ALL =====================
    public List<CategoryResponseDTO> getAllCategories() {

        List<Category> categories = categoryRepository.findAll();
        List<CategoryResponseDTO> responseDTOs = new ArrayList<>();

        for (Category category : categories) {
            responseDTOs.add(mapToResponseDTO(category));
        }

        return responseDTOs;
    }

    // ===================== GET BY ID =====================
    public CategoryResponseDTO getCategoryById(Long id) {

        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }

        Category category = categoryRepository.findById(id).get();
        return mapToResponseDTO(category);
    }

    // ===================== UPDATE =====================
    public CategoryResponseDTO updateCategory(CategoryRequestDTO request, String username) {

        // Check categoryId is provided
        if (request.getCategoryId() == null) {
            throw new RuntimeException("Category id cannot be empty");
        }

        // Check name is not empty
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("Category name cannot be empty");
        }

        // Check slug is not empty
        if (request.getSlug() == null || request.getSlug().trim().isEmpty()) {
            throw new RuntimeException("Category slug cannot be empty");
        }

        // Check if category exists
        if (!categoryRepository.existsById(request.getCategoryId())) {
            throw new ResourceNotFoundException("Category not found with id: " + request.getCategoryId());
        }

        // Check slug is not taken by another category
        if (categoryRepository.existsBySlugAndCategoryIdNot(request.getSlug(), request.getCategoryId())) {
            throw new RuntimeException("Slug already exists: " + request.getSlug());
        }

        Category category = categoryRepository.findById(request.getCategoryId()).get();
        category.setName(request.getName());
        category.setSlug(request.getSlug());
        category.setModifiedBy(username);

        // Update parent if parentId is provided
        if (request.getParentId() != null) {
            if (!categoryRepository.existsById(request.getParentId())) {
                throw new ResourceNotFoundException("Parent category not found with id: " + request.getParentId());
            }
            Category parent = categoryRepository.findById(request.getParentId()).get();
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        Category updatedCategory = categoryRepository.save(category);
        return mapToResponseDTO(updatedCategory);
    }

    // ===================== DELETE =====================
    public String deleteCategory(CategoryRequestDTO request, String username) {

        // Check categoryId is provided
        if (request.getCategoryId() == null) {
            throw new RuntimeException("Category id cannot be empty");
        }

        // Check if category exists
        if (!categoryRepository.existsById(request.getCategoryId())) {
            throw new ResourceNotFoundException("Category not found with id: " + request.getCategoryId());
        }

        // Check if category has children
        Category category = categoryRepository.findById(request.getCategoryId()).get();
        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            throw new RuntimeException("Cannot delete category, it has subcategories linked to it");
        }

        // Soft delete - just set isActive to false
        category.setIsActive(false);
        category.setModifiedBy(username);
        categoryRepository.save(category);

        return "Category deleted successfully";
    }

    // ===================== HELPER - Map Entity to ResponseDTO =====================
    private CategoryResponseDTO mapToResponseDTO(Category category) {

        CategoryResponseDTO responseDTO = new CategoryResponseDTO();
        responseDTO.setCategoryId(category.getCategoryId());
        responseDTO.setName(category.getName());
        responseDTO.setSlug(category.getSlug());

        if (category.getParent() != null) {
            responseDTO.setParentId(category.getParent().getCategoryId());
        }

        return responseDTO;
    }
}