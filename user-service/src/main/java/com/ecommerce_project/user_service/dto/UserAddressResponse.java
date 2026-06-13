package com.ecommerce_project.user_service.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class UserAddressResponse {

    private UUID addressId;
    private String fullName;
    private String phone;
    private String addressLine;
    private String city;
    private String state;
    private String pincode;
    private String country;
    private Boolean isDefault;
    private Boolean isActive;
}