package com.jeyah.jeyahshopapi.user;

import com.jeyah.jeyahshopapi.exception.ErrorResponse;

import java.util.stream.Collectors;

public class UserMapper {

    public static UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .enabled(user.isEnabled())
                .hasPassword(user.getPassword() != null)
                .roles(user.getRoles()
                        .stream()
                        .map(role -> role.getName()) // Assuming Role has getName()
                        .collect(Collectors.toList()))
                .address(user.getAddress() != null
                        ? AddressResponse.builder()
                        .id(user.getAddress().getId())
                        .postalCode(user.getAddress().getPostalCode())
                        .city(user.getAddress().getCity())
                        .street(user.getAddress().getStreet())
                        .build()
                        : null)
                .creationDate(user.getCreationDate())
                .lastModifiedDate(user.getLastModifiedDate())
                .build();
    }

    // ✅ New method for mapping error messages
    public static ErrorResponse toErrorResponse(String message) {
        return new ErrorResponse(message);
    }

}
