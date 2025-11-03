package com.jeyah.jeyahshopapi.user;

import com.jeyah.jeyahshopapi.exception.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    @PreAuthorize("#id == @securityUtil.getCurrentUserId()")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody UpdateUserRequest request) {
        try {
            User updatedUser = userService.updateUser(id, request);
            UserResponse response = UserMapper.toResponse(updatedUser);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erreur interne, veuillez réessayer"));
        }
    }

    // Update phone
    @PatchMapping("/{id}/phone")
    @PreAuthorize("#id == @securityUtil.getCurrentUserId()")
    public ResponseEntity<UserResponse> updatePhone(@PathVariable Integer id, @RequestBody UpdatePhoneRequest request) {
        User updatedUser = userService.updatePhone(id, request.getPhone());
        UserResponse response = UserMapper.toResponse(updatedUser);

        return ResponseEntity.ok(response);
    }

    // Update name
    @PatchMapping("/{id}/first-name")
    @PreAuthorize("#id == @securityUtil.getCurrentUserId()")
    public ResponseEntity<UserResponse> updateFirstName(@PathVariable Integer id, @RequestBody UpdateFirstNameRequest request) {
        User updatedUser = userService.updateFirstName(id, request.getFirstName());
        UserResponse response = UserMapper.toResponse(updatedUser);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/last-name")
    @PreAuthorize("#id == @securityUtil.getCurrentUserId()")
    public ResponseEntity<UserResponse> updateName(@PathVariable Integer id, @RequestBody UpdateLastNameRequest request) {
        User updatedUser = userService.updateLastName(id, request.getLastName());
        UserResponse response = UserMapper.toResponse(updatedUser);

        return ResponseEntity.ok(response);
    }


    // Update address
    @PatchMapping("/{id}/address")
    @PreAuthorize("#id == @securityUtil.getCurrentUserId()")

    public ResponseEntity<?> updateAddress(@PathVariable Integer id, @RequestBody UpdateAddressRequest request) {
        try {
            User updatedUser = userService.updateAddress(id, request);
            UserResponse response = UserMapper.toResponse(updatedUser);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(UserMapper.toErrorResponse(ex.getMessage())); // you can create a simple error response DTO
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(UserMapper.toErrorResponse("Erreur interne, veuillez réessayer"));
        }
    }

    @PatchMapping("/{id}/password")
    @PreAuthorize("#id == @securityUtil.getCurrentUserId()")
    public ResponseEntity<String> updatePassword(
            @PathVariable Integer id,
            @RequestBody UpdatePasswordRequest request) {
        userService.updatePassword(id, request);
        return ResponseEntity.ok("Mot de passe mis à jour avec succès");
    }


}
