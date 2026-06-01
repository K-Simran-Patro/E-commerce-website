package com.ecommerce_project.product_service.dto.variant;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VariantResponseDTO {

    private Long variantId;
    private Long productId;
    private String sku;
    private String color;
    private String size;
    private BigDecimal price;
    private Boolean isActive;
}