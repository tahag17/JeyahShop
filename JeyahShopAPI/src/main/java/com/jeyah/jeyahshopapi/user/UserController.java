package com.jeyah.jeyahshopapi.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<User> updatePhone(@PathVariable Integer id, @RequestBody UpdatePhoneRequest request) {
        User updatedUser = userService.updatePhone(id, request.getPhone());
        return ResponseEntity.ok(updatedUser);
    }

    // Update name
    @PatchMapping("/{id}/first-name")
    public ResponseEntity<User> updateFirstName(@PathVariable Integer id, @RequestBody UpdateFirstNameRequest request) {
        User updatedUser = userService.updateFirstName(id, request.getFirstName());
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{id}/last-name")
    public ResponseEntity<User> updateName(@PathVariable Integer id, @RequestBody UpdateLastNameRequest request) {
        User updatedUser = userService.updateLastName(id, request.getLastName());
        return ResponseEntity.ok(updatedUser);
    }


    // Update address
    @PatchMapping("/{id}/address")
    public ResponseEntity<User> updateAddress(@PathVariable Integer id, @RequestBody UpdateAddressRequest request) {
        User updatedUser = userService.updateAddress(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<String> updatePassword(
            @PathVariable Integer id,
            @RequestBody UpdatePasswordRequest request) {
        userService.updatePassword(id, request);
        return ResponseEntity.ok("Mot de passe mis à jour avec succès");
    }


}
