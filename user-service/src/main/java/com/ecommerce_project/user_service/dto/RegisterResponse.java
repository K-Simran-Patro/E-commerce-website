package com.ecommerce_project.user_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterResponse {

    private String message;
    private String email;
    private String role;

    public RegisterResponse(String message, String email, String role) {
        this.message = message;
        this.email = email;
        this.role = role;
    }
}