package com.ecommerce_project.product_service.dto.variant;



import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariantRequestDTO {
    private String sku;
    private String color;
    private String size;
    private BigDecimal price;
    private Boolean isActive;
}
