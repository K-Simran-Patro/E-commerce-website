package com.ecommerce_project.user_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserWishlistDeleteRequest {

    @NotNull(message = "Variant id is required")
    private Long variantId;
}