package com.jeyah.jeyahshopapi.user;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UserResponse {
    private Integer id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phone;
    private boolean enabled;
    private boolean hasPassword;
    private List<String> roles;
    private AddressResponse address;
    private LocalDateTime creationDate;
    private LocalDateTime lastModifiedDate;
}
