package com.ecommerce_project.user_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterResponse {

    private String message;


    public RegisterResponse(String message){
        this.message = message;
        
    }
}