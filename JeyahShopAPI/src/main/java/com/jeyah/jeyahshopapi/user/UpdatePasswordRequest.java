package com.jeyah.jeyahshopapi.user;

import lombok.Data;

@Data
public class UpdatePasswordRequest {
    private String oldPassword;  // optional if setting for the first time
    private String newPassword;

}
