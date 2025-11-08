package com.jeyah.jeyahshopapi.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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


    @Value("${frontend.url}")
    private String frontendUrl; // inject frontend URL


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        System.out.println("=== OAuth2LoginSuccessHandler HIT ===");

        CustomUserPrincipal principal;
        if (authentication.getPrincipal() instanceof CustomUserPrincipal) {
            principal = (CustomUserPrincipal) authentication.getPrincipal();
            System.out.println("Principal type: CustomUserPrincipal");
        } else if (authentication.getPrincipal() instanceof org.springframework.security.oauth2.core.oidc.user.OidcUser) {
            principal = (CustomUserPrincipal) ((org.springframework.security.oauth2.core.oidc.user.OidcUser) authentication.getPrincipal())
                    .getAttribute("principal");
            System.out.println("Principal type: OidcUser (from attribute)");
        } else {
            throw new IllegalStateException("Unknown principal type: " + authentication.getPrincipal().getClass());
        }

        // Put authentication into SecurityContext & session
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

        User user = principal.getUser();
        String userJson = objectMapper.writeValueAsString(UserMapper.toResponse(user));
        System.out.println("User JSON to send: " + userJson);
        System.out.println("Frontend URL: " + frontendUrl);

        // Return HTML that posts message to opener and closes popup
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("""
            <html>
            <body>
              <script>
                window.opener.postMessage(%s, '%s');
                window.close();
              </script>
            </body>
            </html>
        """.formatted(userJson, frontendUrl));
        response.getWriter().flush();

        System.out.println("PostMessage script sent to popup");
    }

}
