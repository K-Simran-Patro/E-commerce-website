package com.ecommerce_project.product_service.dto.product;

import com.ecommerce_project.product_service.dto.variant.VariantRequestDTO;
import lombok.*;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {
    private Long categoryId;
    private Long brandId;
    private String name;
    private String description;
    private String mainImageKey;
    private String status;
    private List<VariantRequestDTO> variants;
}




