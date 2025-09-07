package com.jeyah.jeyahshopapi.utils;

import com.jeyah.jeyahshopapi.user.CustomUserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

@Component("securityUtil")
public class SecurityUtil {
    public Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();

        if (principal instanceof CustomUserPrincipal) {
            return ((CustomUserPrincipal) principal).getId();
        }

        if (principal instanceof OidcUser) {
            OidcUser oidcUser = (OidcUser) principal;
            CustomUserPrincipal customPrincipal = (CustomUserPrincipal) oidcUser.getAttributes().get("principal");
            if (customPrincipal != null) {
                return customPrincipal.getId();
            }
        }

        throw new IllegalStateException("Unknown principal type: " + principal.getClass());
    }
}
