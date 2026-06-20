package com.ecommerce_project.user_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class UserCartDeleteRequest {

    @NotNull(message = "Cart id is required")
    private UUID cartId;
}