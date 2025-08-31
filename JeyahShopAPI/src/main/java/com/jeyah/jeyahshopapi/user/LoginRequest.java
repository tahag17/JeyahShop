package com.jeyah.jeyahshopapi.user;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
