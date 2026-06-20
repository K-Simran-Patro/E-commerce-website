package com.ecommerce_project.user_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class UserAddressDeleteRequest {

    @NotNull(message = "Address id is required")
    private UUID addressId;
}