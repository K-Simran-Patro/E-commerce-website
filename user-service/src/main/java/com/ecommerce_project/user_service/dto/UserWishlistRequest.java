package com.ecommerce_project.user_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class UserWishlistRequest {

    @NotNull(message = "Variant id is required")
    private UUID variantId;
}