package com.ecommerce_project.product_service.dto.product;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {
    private Long categoryId;
    private String brandName;
    private String name;
    private String description;
    private String mainImageKey;
    private String status;
}




