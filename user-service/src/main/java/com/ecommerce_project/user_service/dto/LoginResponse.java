package com.ecommerce_project.user_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {

    private String token;

    public LoginResponse(String token, String role) {
        this.token = token;
    }
}