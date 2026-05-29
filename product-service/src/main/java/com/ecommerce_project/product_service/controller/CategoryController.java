package com.ecommerce_project.product_service.controller;

import com.ecommerce_project.product_service.dto.category.CategoryRequestDTO;
import com.ecommerce_project.product_service.entity.Category;
import com.ecommerce_project.product_service.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController //This class handles REST APIs
@RequestMapping("/api/categories") //Base URL for all APIs
public class CategoryController {

    @Autowired //Dependency injection for CategoryRepository creates and injects an instance of CategoryRepository into this controller, allowing it to perform database operations related to categories
    private CategoryRepository categoryRepository; //Used to interact with Category data in the database and perform CRUD operations.

    @PostMapping //This method will run when the client sends a POST request to /api/categories. It creates a new category based on the data provided in the request body and saves it to the database.
    //// Converts request data into a Category object and saves it to the database, returning the saved category with its generated ID.
    public Category createCategory(@RequestBody CategoryRequestDTO request) { // The @RequestBody annotation tells Spring to take the incoming JSON request body and convert it into a CategoryRequestDTO object, which contains the data needed to create a new category. The method then uses this data to create a new Category entity, save it to the database, and return the saved category back to the client.
        Category category = new Category();
        category.setName(request.getName());
        category.setSlug(request.getSlug());
        //// If a parent category ID is provided, fetch the parent category and link it to the current category
        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId()).get();
            category.setParent(parent);
        }
        return categoryRepository.save(category);
    }

    @GetMapping
    //Fetch all categories from the database and send them back to the client as a list of Category objects.
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @GetMapping("/{id}")
    //Fetches a category from the database using its ID and returns it to the client. If the category with the specified ID does not exist, it will throw an exception.
    public Category getCategoryById(@PathVariable Long id) {
        return categoryRepository.findById(id).get();
    }

    @PutMapping("/{id}")
    //Updates an existing category in the database based on the provided ID and request data.
    public Category updateCategory(@PathVariable Long id, @RequestBody CategoryRequestDTO request) {
        Category category = categoryRepository.findById(id).get();
        category.setName(request.getName());
        category.setSlug(request.getSlug());
        return categoryRepository.save(category);
    }

    @DeleteMapping("/{id}")
    //Deletes a category from the database using its ID.
    public String deleteCategory(@PathVariable Long id) {
        categoryRepository.deleteById(id);
        return "Category deleted successfully";
    }
}