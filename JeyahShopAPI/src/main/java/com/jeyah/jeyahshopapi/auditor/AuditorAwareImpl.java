package com.jeyah.jeyahshopapi.auditor;

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

    // Thread-local to store current user ID per request
    static final ThreadLocal<Integer> currentUserId = new ThreadLocal<>();

    public static void setCurrentUserId(Integer userId) {
        currentUserId.set(userId);
    }

    public static void clear() {
        currentUserId.remove();
    }

    @Override
    public Optional<Integer> getCurrentAuditor() {
        return Optional.ofNullable(currentUserId.get());
    }
}
