package com.jeyah.jeyahshopapi.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
//    private final String frontendUrl ="http://localhost:4200";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // The principal should be CustomUserPrincipal (from your CustomOidcUserService)
        CustomUserPrincipal principal;
        if (authentication.getPrincipal() instanceof CustomUserPrincipal) {
            principal = (CustomUserPrincipal) authentication.getPrincipal();
        } else if (authentication.getPrincipal() instanceof org.springframework.security.oauth2.core.oidc.user.OidcUser) {
            // Extract the CustomUserPrincipal stored as an attribute
            principal = (CustomUserPrincipal) ((org.springframework.security.oauth2.core.oidc.user.OidcUser) authentication.getPrincipal())
                    .getAttribute("principal");
        } else {
            throw new IllegalStateException("Unknown principal type: " + authentication.getPrincipal().getClass());
        }

        // Put authentication into the SecurityContext
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        // Persist context into HTTP session (cookie)
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

        // Convert user to response DTO
        User user = principal.getUser();
        String userJson = objectMapper.writeValueAsString(UserMapper.toResponse(user));

        // Send JSON response to frontend
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(userJson);
        response.getWriter().flush();

        // Optional: you can also redirect if needed
        // getRedirectStrategy().sendRedirect(request, response, "/store");
//        getRedirectStrategy().sendRedirect(request, response, frontendUrl + "/dashboard/profile");

    }


}
