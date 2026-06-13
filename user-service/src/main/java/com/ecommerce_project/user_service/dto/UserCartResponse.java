package com.ecommerce_project.user_service.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class UserCartResponse {

    private UUID cartId;
    private UUID variantId;
    private Integer quantity;
    private Boolean isActive;
}