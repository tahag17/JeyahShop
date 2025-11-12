package com.jeyah.jeyahshopapi.auditor;

import com.jeyah.jeyahshopapi.user.CustomUserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuditorAwareFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof CustomUserPrincipal customUser) {
                    AuditorAwareImpl.setCurrentUserId(customUser.getUser().getId());
                }
                // You can add OAuth2User logic here if needed
            }

            filterChain.doFilter(request, response);
        } finally {
            // Clear after request to avoid memory leaks
            AuditorAwareImpl.clear();
        }
    }
}
