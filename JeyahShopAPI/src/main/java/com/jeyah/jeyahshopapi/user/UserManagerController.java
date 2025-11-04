package com.jeyah.jeyahshopapi.user;

import com.jeyah.jeyahshopapi.exception.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/manager/api/users")
@PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
@RequiredArgsConstructor
public class UserManagerController {

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        try {
            Page<User> usersPage = userService.getAllUsers(page, size, sortBy, direction);
            List<UserResponse> userResponses = usersPage.getContent()
                    .stream()
                    .map(UserMapper::toResponse)
                    .toList();

            Map<String, Object> response = new HashMap<>();
            response.put("users", userResponses);
            response.put("currentPage", usersPage.getNumber());
            response.put("totalItems", usersPage.getTotalElements());
            response.put("totalPages", usersPage.getTotalPages());
            response.put("pageSize", usersPage.getSize());

            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(UserMapper.toErrorResponse("Erreur interne, veuillez réessayer"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id, Authentication authentication) {
        User currentUser = ((CustomUserPrincipal) authentication.getPrincipal()).getUser();
        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        boolean isAdmin = currentUser.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_ADMIN"));
        boolean isManager = currentUser.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_MANAGER"));

        if (isManager && targetUser.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_MANAGER") || r.getName().equals("ROLE_ADMIN"))) {
            System.out.println("Pas de permission pour supprimer cet utilisateur");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(UserMapper.toErrorResponse("Vous n'avez pas la permission de supprimer cet utilisateur."));
        }

        userRepository.delete(targetUser);
        return ResponseEntity.ok(Map.of("message", "Utilisateur supprimé"));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<?> toggleEnabled(@PathVariable Integer id, Authentication authentication) {
        User currentUser = ((CustomUserPrincipal) authentication.getPrincipal()).getUser();
        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        boolean isAdmin = currentUser.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_ADMIN"));
        boolean isManager = currentUser.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_MANAGER"));

        if (isManager && targetUser.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_MANAGER") || r.getName().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(UserMapper.toErrorResponse("Vous n'avez pas la permission de modifier cet utilisateur."));
        }

        targetUser.setEnabled(!targetUser.isEnabled());
        userRepository.save(targetUser);

        return ResponseEntity.ok(UserMapper.toResponse(targetUser));
    }


    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<?> updateUserAsManager(
            @PathVariable Integer id,
            @RequestBody UpdateUserRequest request,
            Authentication authentication) {

        try {
            // Get the currently authenticated user
            User currentUser = ((CustomUserPrincipal) authentication.getPrincipal()).getUser();

            // Find the target user
            User targetUser = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            boolean isAdmin = currentUser.getRoles().stream()
                    .anyMatch(r -> r.getName().equals("ROLE_ADMIN"));
            boolean isManager = currentUser.getRoles().stream()
                    .anyMatch(r -> r.getName().equals("ROLE_MANAGER"));

            // Role-based restriction: Manager cannot edit other managers or admins
            if (isManager && targetUser.getRoles().stream()
                    .anyMatch(r -> r.getName().equals("ROLE_MANAGER") || r.getName().equals("ROLE_ADMIN"))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(UserMapper.toErrorResponse("Vous n'avez pas la permission de modifier cet utilisateur."));
            }

            // Admin can edit anyone, manager can edit only users
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



}
