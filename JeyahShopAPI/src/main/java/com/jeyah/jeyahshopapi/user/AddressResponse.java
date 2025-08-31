package com.jeyah.jeyahshopapi.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressResponse {
    private Integer id;
    private Integer postalCode;
    private String city;
    private String street;
}
