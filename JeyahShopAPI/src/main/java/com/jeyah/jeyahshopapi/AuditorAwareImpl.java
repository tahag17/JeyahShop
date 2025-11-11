package com.jeyah.jeyahshopapi;

import com.jeyah.jeyahshopapi.user.User;
import com.jeyah.jeyahshopapi.user.UserRepository;
import com.jeyah.jeyahshopapi.user.CustomUserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<Integer> {

    private final UserRepository userRepository;

    @Autowired
    public AuditorAwareImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

//    @Override
//    public Optional<Integer> getCurrentAuditor() {
//        try {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//            System.out.println("\n============================");
//            System.out.println("[AuditorAware] 🔍 Checking current auditor...");
//
//            if (authentication == null) {
//                System.out.println("[AuditorAware] ❌ Authentication is NULL");
//                System.out.println("============================\n");
//                return Optional.empty();
//            }
//
//            System.out.println("[AuditorAware] ✅ Authentication object found:");
//            System.out.println("  - Class: " + authentication.getClass().getName());
//            System.out.println("  - Principal: " + authentication.getPrincipal());
//            System.out.println("  - Name: " + authentication.getName());
//            System.out.println("  - Credentials: " + authentication.getCredentials());
//            System.out.println("  - Authorities: " + authentication.getAuthorities());
//            System.out.println("  - Authenticated: " + authentication.isAuthenticated());
//            System.out.println("============================");
//
//            if (!authentication.isAuthenticated()) {
//                System.out.println("[AuditorAware] ⚠️ User is not authenticated");
//                System.out.println("============================\n");
//                return Optional.empty();
//            }
//
//            // Extract user ID from your CustomUserPrincipal
//            Object principal = authentication.getPrincipal();
//            if (principal instanceof CustomUserPrincipal customUserPrincipal) {
//                Integer userId = customUserPrincipal.getUser().getId();
//                System.out.println("[AuditorAware] 👤 Current auditor user ID: " + userId);
//                System.out.println("============================\n");
//                return Optional.ofNullable(userId);
//            } else {
//                System.out.println("[AuditorAware] ⚠️ Principal is NOT an instance of CustomUserPrincipal");
//                System.out.println("============================\n");
//                return Optional.empty();
//            }
//
//        } catch (Exception e) {
//            System.out.println("[AuditorAware] 💥 Exception occurred:");
//            e.printStackTrace();
//            System.out.println("============================\n");
//            return Optional.empty();
//        }
//    }

    @Override
    public Optional<Integer> getCurrentAuditor() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                System.out.println("[AuditorAware] ⚠️ No authenticated user found.");
                return Optional.empty();
            }

            Object principal = authentication.getPrincipal();

            // Case 1️⃣: CustomUserPrincipal (form login)
            if (principal instanceof CustomUserPrincipal customUserPrincipal) {
                Integer userId = customUserPrincipal.getUser().getId();
                System.out.println("[AuditorAware] 👤 Found user ID (CustomUserPrincipal): " + userId);
                return Optional.of(userId);
            }

            // Case 2️⃣: OAuth2User (Google / Keycloak)
            if (principal instanceof OAuth2User oauth2User) {
                String email = oauth2User.getAttribute("email");
                if (email == null) {
                    System.out.println("[AuditorAware] ⚠️ OAuth2User missing email attribute");
                    return Optional.empty();
                }

                return userRepository.findByEmail(email)
                        .map(user -> {
                            System.out.println("[AuditorAware] 👤 Found user ID (OAuth2User): " + user.getId());
                            return user.getId();
                        });
            }

            System.out.println("[AuditorAware] ⚠️ Unknown principal type: " + principal.getClass().getName());
            return Optional.empty();

        } catch (Exception e) {
            System.out.println("[AuditorAware] 💥 Exception occurred:");
            e.printStackTrace();
            return Optional.empty();
        }
    }

}
