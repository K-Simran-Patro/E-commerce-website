package com.ecommerce_project.product_service.service;

import com.ecommerce_project.product_service.dto.category.CategoryRequestDTO;
import com.ecommerce_project.product_service.dto.category.CategoryResponseDTO;
import com.ecommerce_project.product_service.entity.Category;
import com.ecommerce_project.product_service.exception.ResourceNotFoundException;
import com.ecommerce_project.product_service.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    // Logger for this class — prints messages to console/Render logs
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    @Autowired
    private CategoryRepository categoryRepository;

    // CREATE 
    public CategoryResponseDTO createCategory(CategoryRequestDTO request, String username) //Handles the creation of a new category. It takes a CategoryRequestDTO object (which contains the details of the category to be created) and the username of the user making the request. It performs various checks (like if the slug is unique, if the parent category exists) and then saves the new category to the database. Finally, it returns a CategoryResponseDTO object with the details of the created category.
    {

        logger.info("Creating category with name: {} by user: {}", request.getName(), username); // Logs an informational message indicating that a category creation request has been received, along with the name of the category being created and the username of the user making the request.

        // Check if slug already exists
        if (categoryRepository.existsBySlug(request.getSlug())) // Checks if a category with the same slug already exists in the database. If it does, it logs a warning message and throws a RuntimeException to indicate that the slug is already taken.
            {
            logger.warn("Slug already exists: {}", request.getSlug());
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
                logger.error("Parent category not found with id: {}", request.getParentId());
                throw new ResourceNotFoundException("Parent category not found with id: " + request.getParentId());
            }
            Category parent = categoryRepository.findById(request.getParentId()).get();
            category.setParent(parent);
        }

        Category savedCategory = categoryRepository.save(category);
        logger.info("Category created successfully with id: {}", savedCategory.getCategoryId());
        return mapToResponseDTO(savedCategory);
    }

    //  GET ALL 
    public List<CategoryResponseDTO> getAllCategories() {

        logger.info("Fetching all categories");

        List<Category> categories = categoryRepository.findAll();
        List<CategoryResponseDTO> responseDTOs = new ArrayList<>();

        for (Category category : categories) {
            responseDTOs.add(mapToResponseDTO(category));
        }

        logger.info("Total categories fetched: {}", responseDTOs.size());
        return responseDTOs;
    }

    // GET BY ID
    public CategoryResponseDTO getCategoryById(CategoryRequestDTO request) {

        logger.info("Fetching category with id: {}", request.getCategoryId());

        if (!categoryRepository.existsById(request.getCategoryId())) {
         logger.error("Category not found with id: {}", request.getCategoryId());
         throw new ResourceNotFoundException("Category not found with id: " + request.getCategoryId());
        }

        Category category = categoryRepository.findById(request.getCategoryId()).get();

        logger.info("Category fetched successfully with id: {}", request.getCategoryId());
            return mapToResponseDTO(category);
    }

    // UPDATE
    public CategoryResponseDTO updateCategory(CategoryRequestDTO request, String username) {

        logger.info("Updating category with id: {} by user: {}", request.getCategoryId(), username);

        // Check categoryId is provided
        if (request.getCategoryId() == null) {
            logger.warn("Category id is missing in update request");
            throw new RuntimeException("Category id cannot be empty");
        }

        // Check if category exists
        if (!categoryRepository.existsById(request.getCategoryId())) {
            logger.error("Category not found with id: {}", request.getCategoryId());
            throw new ResourceNotFoundException("Category not found with id: " + request.getCategoryId());
        }

        // Check slug is not taken by another category
        if (categoryRepository.existsBySlugAndCategoryIdNot(request.getSlug(), request.getCategoryId())) {
            logger.warn("Slug already exists: {}", request.getSlug());
            throw new RuntimeException("Slug already exists: " + request.getSlug());
        }

        Category category = categoryRepository.findById(request.getCategoryId()).get();
        category.setName(request.getName());
        category.setSlug(request.getSlug());
        category.setModifiedBy(username);

        // Update parent if parentId is provided
        if (request.getParentId() != null) {
            if (!categoryRepository.existsById(request.getParentId())) {
                logger.error("Parent category not found with id: {}", request.getParentId());
                throw new ResourceNotFoundException("Parent category not found with id: " + request.getParentId());
            }
            Category parent = categoryRepository.findById(request.getParentId()).get();
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        Category updatedCategory = categoryRepository.save(category);
        logger.info("Category updated successfully with id: {}", updatedCategory.getCategoryId());
        return mapToResponseDTO(updatedCategory);
    }

    //DELETE 
    public String deleteCategory(CategoryRequestDTO request, String username) {

        logger.info("Deleting category with id: {} by user: {}", request.getCategoryId(), username);

        // Check categoryId is provided
        if (request.getCategoryId() == null) {
            logger.warn("Category id is missing in delete request");
            throw new RuntimeException("Category id cannot be empty");
        }

        // Check if category exists
        if (!categoryRepository.existsById(request.getCategoryId())) {
            logger.error("Category not found with id: {}", request.getCategoryId());
            throw new ResourceNotFoundException("Category not found with id: " + request.getCategoryId());
        }

        // Check if category has children
        Category category = categoryRepository.findById(request.getCategoryId()).get();
        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            logger.warn("Cannot delete category id: {} — has subcategories", request.getCategoryId());
            throw new RuntimeException("Cannot delete category, it has subcategories linked to it");
        }

        // Soft delete - just set isActive to false
        category.setIsActive(false);
        category.setModifiedBy(username);
        categoryRepository.save(category);

        logger.info("Category soft deleted successfully with id: {}", request.getCategoryId());
        return "Category deleted successfully";
    }

    //  HELPER - Map Entity to ResponseDTO 
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