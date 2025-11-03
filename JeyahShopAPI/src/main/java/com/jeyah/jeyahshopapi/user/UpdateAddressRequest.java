package com.jeyah.jeyahshopapi.user;

import lombok.Data;

@Data
public class UpdateAddressRequest {
    private String street;
    private String city;
    private String postalCode;
}
