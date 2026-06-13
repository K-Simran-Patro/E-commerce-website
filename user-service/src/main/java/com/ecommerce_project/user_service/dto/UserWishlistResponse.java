package com.ecommerce_project.user_service.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
public class UserWishlistResponse {

    private UUID wishlistId;
    private UUID variantId;
    private Boolean isActive;
    private OffsetDateTime createdAt;
}