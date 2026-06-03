package com.ecommerce_project.product_service.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequestDTO {

    private Long categoryId;      // not required — only for update and delete

    @NotBlank(message = "Category name cannot be empty")
    private String name;

    @NotBlank(message = "Category slug cannot be empty")
    private String slug;

    private Long parentId;        // not required — only for sub categories
}