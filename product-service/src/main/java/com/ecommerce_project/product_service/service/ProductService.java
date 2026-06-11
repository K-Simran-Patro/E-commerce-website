package com.ecommerce_project.product_service.service;

import com.ecommerce_project.product_service.dto.product.ProductRequestDTO;
import com.ecommerce_project.product_service.dto.product.ProductResponseDTO;
import com.ecommerce_project.product_service.entity.Category;
import com.ecommerce_project.product_service.entity.Product;
import com.ecommerce_project.product_service.exception.ResourceNotFoundException;
import com.ecommerce_project.product_service.repository.CategoryRepository;
import com.ecommerce_project.product_service.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    // Logger for this class
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // ===================== CREATE =====================
    public ProductResponseDTO createProduct(ProductRequestDTO request, String username) {

        logger.info("Creating product with name: {} by user: {}", request.getName(), username);

        // Check if category exists
        if (!categoryRepository.existsById(request.getCategoryId())) {
            logger.error("Category not found with id: {}", request.getCategoryId());
            throw new ResourceNotFoundException("Category not found with id: " + request.getCategoryId());
        }

        // Check if product name already exists in same category
        if (productRepository.existsByNameAndCategoryCategoryId(request.getName(), request.getCategoryId())) {
            logger.warn("Product already exists in category id: {} with name: {}", request.getCategoryId(), request.getName());
            throw new RuntimeException("Product already exists in this category: " + request.getName());
        }

        Category category = categoryRepository.findById(request.getCategoryId()).get();

        Product product = new Product();
        product.setCategory(category);
        product.setBrandName(request.getBrandName());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setMainImageKey(request.getMainImageKey());
        product.setIsActive(true);
        product.setCreatedBy(username);
        product.setModifiedBy(username);

        Product savedProduct = productRepository.save(product);
        logger.info("Product created successfully with id: {}", savedProduct.getProductId());
        return mapToResponseDTO(savedProduct);
    }

    // ===================== GET ALL =====================
    public List<ProductResponseDTO> getAllProducts() {

        logger.info("Fetching all products");

        List<Product> products = productRepository.findAll();
        List<ProductResponseDTO> responseDTOs = new ArrayList<>();

        for (Product product : products) {
            responseDTOs.add(mapToResponseDTO(product));
        }

        logger.info("Total products fetched: {}", responseDTOs.size());
        return responseDTOs;
    }

    // ===================== GET BY ID =====================
    public ProductResponseDTO getProductById(Long id) {

        logger.info("Fetching product with id: {}", id);

        if (!productRepository.existsById(id)) {
            logger.error("Product not found with id: {}", id);
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }

        Product product = productRepository.findById(id).get();
        return mapToResponseDTO(product);
    }

    // ===================== UPDATE =====================
    public ProductResponseDTO updateProduct(ProductRequestDTO request, String username) {

        logger.info("Updating product with id: {} by user: {}", request.getProductId(), username);

        // Check productId is provided
        if (request.getProductId() == null) {
            logger.warn("Product id is missing in update request");
            throw new RuntimeException("Product id cannot be empty");
        }

        // Check if product exists
        if (!productRepository.existsById(request.getProductId())) {
            logger.error("Product not found with id: {}", request.getProductId());
            throw new ResourceNotFoundException("Product not found with id: " + request.getProductId());
        }

        // Check if product name already exists in same category for another product
        if (productRepository.existsByNameAndCategoryCategoryIdAndProductIdNot(request.getName(), request.getCategoryId(), request.getProductId())) {
            logger.warn("Product name already exists in category: {}", request.getName());
            throw new RuntimeException("Product already exists in this category: " + request.getName());
        }

        Product product = productRepository.findById(request.getProductId()).get();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setMainImageKey(request.getMainImageKey());
        product.setBrandName(request.getBrandName());
        product.setModifiedBy(username);

        // Update category if categoryId is provided
        if (request.getCategoryId() != null) {
            if (!categoryRepository.existsById(request.getCategoryId())) {
                logger.error("Category not found with id: {}", request.getCategoryId());
                throw new ResourceNotFoundException("Category not found with id: " + request.getCategoryId());
            }
            Category category = categoryRepository.findById(request.getCategoryId()).get();
            product.setCategory(category);
        }

        Product updatedProduct = productRepository.save(product);
        logger.info("Product updated successfully with id: {}", updatedProduct.getProductId());
        return mapToResponseDTO(updatedProduct);
    }

    // ===================== DELETE =====================
    public String deleteProduct(ProductRequestDTO request, String username) {

        logger.info("Deleting product with id: {} by user: {}", request.getProductId(), username);

        // Check productId is provided
        if (request.getProductId() == null) {
            logger.warn("Product id is missing in delete request");
            throw new RuntimeException("Product id cannot be empty");
        }

        // Check if product exists
        if (!productRepository.existsById(request.getProductId())) {
            logger.error("Product not found with id: {}", request.getProductId());
            throw new ResourceNotFoundException("Product not found with id: " + request.getProductId());
        }

        // Soft delete - just set isActive to false
        Product product = productRepository.findById(request.getProductId()).get();
        product.setIsActive(false);
        product.setModifiedBy(username);
        productRepository.save(product);

        logger.info("Product soft deleted successfully with id: {}", request.getProductId());
        return "Product deleted successfully";
    }

    // ===================== HELPER - Map Entity to ResponseDTO =====================
    private ProductResponseDTO mapToResponseDTO(Product product) {

        ProductResponseDTO responseDTO = new ProductResponseDTO();
        responseDTO.setProductId(product.getProductId());
        responseDTO.setCategoryId(product.getCategory().getCategoryId());
        responseDTO.setBrandName(product.getBrandName());
        responseDTO.setName(product.getName());
        responseDTO.setDescription(product.getDescription());
        responseDTO.setMainImageKey(product.getMainImageKey());
        responseDTO.setIsActive(product.getIsActive());

        return responseDTO;
    }
}