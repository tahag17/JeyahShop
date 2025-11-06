package com.jeyah.jeyahshopapi.auth;

import com.jeyah.jeyahshopapi.user.CustomUserPrincipal;
import com.jeyah.jeyahshopapi.user.User;
import com.jeyah.jeyahshopapi.user.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class AuthUtils {

    /**
     * Retrieves the currently authenticated User, whether from form-based or OAuth2 login.
     *
     * @param userRepository the UserRepository to look up users by email (for OAuth2 logins)
     * @return the authenticated User entity
     * @throws RuntimeException if no valid user can be found
     */
    public static User getCurrentUser(UserRepository userRepository) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Form-based authentication
        if (principal instanceof CustomUserPrincipal customUserPrincipal) {
            return customUserPrincipal.getUser();
        }

        // OAuth2 authentication (Google, Keycloak, etc.)
        else if (principal instanceof OAuth2User oauth2User) {
            String email = oauth2User.getAttribute("email");
            if (email == null) {
                throw new RuntimeException("OAuth2 principal does not contain an email attribute");
            }

            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found for email: " + email));
        }

        // Unknown principal type
        throw new RuntimeException("Unknown principal type: " + principal.getClass());
    }
}
