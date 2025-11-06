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

    @Override
    public Optional<Integer> getCurrentAuditor() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            System.out.println("\n============================");
            System.out.println("[AuditorAware] 🔍 Checking current auditor...");

            if (authentication == null) {
                System.out.println("[AuditorAware] ❌ Authentication is NULL");
                System.out.println("============================\n");
                return Optional.empty();
            }

            System.out.println("[AuditorAware] ✅ Authentication object found:");
            System.out.println("  - Class: " + authentication.getClass().getName());
            System.out.println("  - Principal: " + authentication.getPrincipal());
            System.out.println("  - Name: " + authentication.getName());
            System.out.println("  - Credentials: " + authentication.getCredentials());
            System.out.println("  - Authorities: " + authentication.getAuthorities());
            System.out.println("  - Authenticated: " + authentication.isAuthenticated());
            System.out.println("============================");

            if (!authentication.isAuthenticated()) {
                System.out.println("[AuditorAware] ⚠️ User is not authenticated");
                System.out.println("============================\n");
                return Optional.empty();
            }

            // Extract user ID from your CustomUserPrincipal
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserPrincipal customUserPrincipal) {
                Integer userId = customUserPrincipal.getUser().getId();
                System.out.println("[AuditorAware] 👤 Current auditor user ID: " + userId);
                System.out.println("============================\n");
                return Optional.ofNullable(userId);
            } else {
                System.out.println("[AuditorAware] ⚠️ Principal is NOT an instance of CustomUserPrincipal");
                System.out.println("============================\n");
                return Optional.empty();
            }

        } catch (Exception e) {
            System.out.println("[AuditorAware] 💥 Exception occurred:");
            e.printStackTrace();
            System.out.println("============================\n");
            return Optional.empty();
        }
    }


}
