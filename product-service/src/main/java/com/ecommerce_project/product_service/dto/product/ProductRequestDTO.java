package com.ecommerce_project.product_service.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {

    private Long productId;        // not required — only for update and delete

    @NotNull(message = "Category id cannot be empty")
    private Long categoryId;

    private String brandName;      // not required — brand is optional

    @NotBlank(message = "Product name cannot be empty")
    private String name;

    private String description;    // not required
    private String mainImageKey;   // not required
    private Boolean isActive;      // not required
}