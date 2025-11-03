package com.jeyah.jeyahshopapi.user;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private String phone;

    // Address fields
    private String street;
    private String city;
    private String postalCode;
}
