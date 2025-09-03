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

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        CustomUserPrincipal principal;
        if (authentication.getPrincipal() instanceof CustomUserPrincipal) {
            principal = (CustomUserPrincipal) authentication.getPrincipal();
        } else if (authentication.getPrincipal() instanceof org.springframework.security.oauth2.core.oidc.user.OidcUser) {
            principal = (CustomUserPrincipal) ((org.springframework.security.oauth2.core.oidc.user.OidcUser) authentication.getPrincipal())
                    .getAttribute("principal");
        } else {
            throw new IllegalStateException("Unknown principal type: " + authentication.getPrincipal().getClass());
        }

        // Put authentication into SecurityContext & session
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
        request.getSession(true)
                .setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

        // Convert user to response JSON
        User user = principal.getUser();
        String userJson = objectMapper.writeValueAsString(UserMapper.toResponse(user));

        // Return HTML that posts message to opener and closes popup
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("""
            <html>
            <body>
              <script>
                window.opener.postMessage(%s, 'http://localhost:4200');
                window.close();
              </script>
            </body>
            </html>
        """.formatted(userJson));
        response.getWriter().flush();

        // DO NOT call sendRedirect here — remove any redirect
    }
}
