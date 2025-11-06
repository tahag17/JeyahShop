package com.jeyah.jeyahshopapi.product;

import com.jeyah.jeyahshopapi.common.PageResponse;
import com.jeyah.jeyahshopapi.exception.ErrorResponse;
import com.jeyah.jeyahshopapi.user.CustomUserPrincipal;
import com.jeyah.jeyahshopapi.user.User;
import com.jeyah.jeyahshopapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/manager/api/products")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
public class ProductManagerController {

    private final ProductService productService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // 1️⃣ Get all products (paginated)
    @GetMapping
    public ResponseEntity<?> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        try {
            PageResponse<SimpleProductResponse> products = productService.findAllProducts(page, size);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erreur lors de la récupération des produits."));
        }
    }

    // 2️⃣ Get product by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Integer id) {
        try {
            ProductResponse product = productService.findProductById(id);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Produit non trouvé."));
        }
    }

    // 3️⃣ Add product with image upload
    @PostMapping(consumes = {"multipart/form-data"})
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<?> addProduct(
            @RequestPart("product") ProductRequest request,
            @RequestPart(value = "images", required = false) MultipartFile[] images) {

        try {
            // Get current principal
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User currentUser = null;

            // Handle form-based login
            if (principal instanceof CustomUserPrincipal customUserPrincipal) {
                currentUser = customUserPrincipal.getUser();
                System.out.println("🔍 Authenticated via form login");
            }
            // Handle OAuth2 login (Google, etc.)
            else if (principal instanceof OAuth2User oauth2User) {
                String email = oauth2User.getAttribute("email");
                System.out.println("🔍 Authenticated via OAuth2. Email: " + email);
                currentUser = userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("User not found for email: " + email));
            }
            else {
                throw new RuntimeException("❌ Unknown principal type: " + principal.getClass());
            }

            System.out.println("✅ Current user ID: " + currentUser.getId() + ", Email: " + currentUser.getEmail());

            // Call service to add product
            Integer productId = productService.addProductWithImages(request, images, currentUser);

            // Return the saved product
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(productService.findProductById(productId));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l’ajout du produit: " + e.getMessage());
        }
    }


    // 4️⃣ Update product
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Integer id,
            @RequestBody ProductRequest request,
            Authentication authentication) {

        try {
            User currentUser = ((CustomUserPrincipal) authentication.getPrincipal()).getUser();
            Product existing = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

            boolean isAdmin = currentUser.hasRole("ROLE_ADMIN");
            boolean isManager = currentUser.hasRole("ROLE_MANAGER");
            boolean isOwner = existing.getUser() != null && existing.getUser().getId().equals(currentUser.getId());

            if (isManager && !isOwner) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("Vous n'avez pas la permission de modifier ce produit."));
            }

            ProductResponse updated = productService.updateProduct(id, request);
            return ResponseEntity.ok(updated);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erreur lors de la mise à jour du produit."));
        }
    }

    // 5️⃣ Delete product
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id, Authentication authentication) {
        try {
            User currentUser = ((CustomUserPrincipal) authentication.getPrincipal()).getUser();
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

            boolean isAdmin = currentUser.hasRole("ROLE_ADMIN");
            boolean isManager = currentUser.hasRole("ROLE_MANAGER");
            boolean isOwner = product.getUser() != null && product.getUser().getId().equals(currentUser.getId());

            if (!isAdmin && !isManager && !isOwner) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("Vous n'avez pas la permission de supprimer ce produit."));
            }

            productRepository.delete(product);

            return ResponseEntity.ok(Map.of("message", "Produit supprimé avec succès."));

        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erreur interne, veuillez réessayer."));
        }
    }

    // 6️⃣ Add images to existing product
    @PostMapping("/{id}/images")
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    public ResponseEntity<?> addImagesToProduct(
            @PathVariable Integer id,
            @RequestPart("images") MultipartFile[] images) {
        try {
            productService.addImagesToProduct(id, images);
            return ResponseEntity.ok(Map.of("message", "Images ajoutées avec succès."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erreur lors de l’ajout des images."));
        }
    }
}
