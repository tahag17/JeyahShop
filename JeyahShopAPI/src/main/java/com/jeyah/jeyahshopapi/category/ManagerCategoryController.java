package com.jeyah.jeyahshopapi.category;

import com.jeyah.jeyahshopapi.auth.AuthUtils;
import com.jeyah.jeyahshopapi.exception.ErrorResponse;
import com.jeyah.jeyahshopapi.user.User;
import com.jeyah.jeyahshopapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/manager/api/categories")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
public class ManagerCategoryController {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    // Create a new category
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody Category category) {
        try {
            User currentUser = AuthUtils.getCurrentUser(userRepository);
            // Optionally, you can track who created the category
            // category.setCreatedBy(currentUser);
            Category saved = categoryRepository.save(category);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erreur lors de la création de la catégorie."));
        }
    }

    // Delete an existing category
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Integer id) {
        try {
            User currentUser = AuthUtils.getCurrentUser(userRepository);
            // Optionally, add ownership or admin check here

            if (!categoryRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Catégorie non trouvée."));
            }

            categoryRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Catégorie supprimée avec succès."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erreur lors de la suppression de la catégorie."));
        }
    }
}
