package com.jeyah.jeyahshopapi.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Update user by ID
    @PatchMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody UpdateUserRequest request) {
        User updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    // Update phone
    @PatchMapping("/{id}/phone")
//    @PreAuthorize("#id == principal.id")
    public ResponseEntity<UserResponse> updatePhone(@PathVariable Integer id, @RequestBody UpdatePhoneRequest request) {
        User updatedUser = userService.updatePhone(id, request.getPhone());
        UserResponse response = UserMapper.toResponse(updatedUser);

        return ResponseEntity.ok(response);
    }

    // Update name
    @PatchMapping("/{id}/first-name")
    public ResponseEntity<UserResponse> updateFirstName(@PathVariable Integer id, @RequestBody UpdateFirstNameRequest request) {
        User updatedUser = userService.updateFirstName(id, request.getFirstName());
        UserResponse response = UserMapper.toResponse(updatedUser);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/last-name")
    public ResponseEntity<UserResponse> updateName(@PathVariable Integer id, @RequestBody UpdateLastNameRequest request) {
        User updatedUser = userService.updateLastName(id, request.getLastName());
        UserResponse response = UserMapper.toResponse(updatedUser);

        return ResponseEntity.ok(response);
    }


    // Update address
    @PatchMapping("/{id}/address")
    public ResponseEntity<UserResponse> updateAddress(@PathVariable Integer id, @RequestBody UpdateAddressRequest request) {
        System.out.println("congratz you have reached the controller!");
        User updatedUser = userService.updateAddress(id, request);
        UserResponse response = UserMapper.toResponse(updatedUser);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<String> updatePassword(
            @PathVariable Integer id,
            @RequestBody UpdatePasswordRequest request) {
        userService.updatePassword(id, request);
        return ResponseEntity.ok("Mot de passe mis à jour avec succès");
    }


}
