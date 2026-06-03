package com.ecommerce_project.product_service.dto.variant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VariantRequestDTO {

    private Long variantId;        // not required — only for update and delete

    private Long productId;        // not required — only for create

    @NotBlank(message = "SKU cannot be empty")
    private String sku;

    private String color;          // not required
    private String size;           // not required

    @NotNull(message = "Price cannot be empty")
    @Positive(message = "Price must be greater than 0")
    private BigDecimal price;

    private Boolean isActive;      // not required
}