package com.ecommerce_project.product_service.dto.product;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {

    private Long productId;
    private Long categoryId;
    private String brandName;
    private String name;
    private String description;
    private String mainImageKey;
    private Boolean isActive;
}