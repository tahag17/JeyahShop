package com.jeyah.jeyahshopapi.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeyah.jeyahshopapi.role.Role;
import com.jeyah.jeyahshopapi.role.RoleRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/public/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
//        try {
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
//            );
//
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//
//            var user = userRepository.findByEmail(request.getEmail())
//                    .orElseThrow(() -> new RuntimeException("User not found"));
//
//            var response = UserMapper.toResponse(user);
//
//            return ResponseEntity.ok(response);
//
//        } catch (BadCredentialsException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(Map.of("error", "Invalid credentials"));
//        }
//    }

//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
//        try {
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
//            );
//
//            // Put authentication into context
//            SecurityContext securityContext = SecurityContextHolder.getContext();
//            securityContext.setAuthentication(authentication);
//
//            // 🔑 Persist context into HTTP session so cookie will be sent back
//            HttpSession session = httpRequest.getSession(true);
//            session.setAttribute(
//                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
//                    securityContext
//            );
//
//            var user = userRepository.findByEmail(request.getEmail())
//                    .orElseThrow(() -> new RuntimeException("User not found"));
//
//            var response = UserMapper.toResponse(user);
//
//            return ResponseEntity.ok(response);
//
//        } catch (BadCredentialsException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(Map.of("error", "Invalid credentials"));
//        }
//    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // Put authentication into context
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);

            // Persist context into HTTP session so cookie will be sent back
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    securityContext
            );

            // Get user from principal instead of querying DB again
            CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
            var response = UserMapper.toResponse(principal.getUser());

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        }
    }



    @PostMapping("/register")
    public Object register(@RequestBody LoginRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return Collections.singletonMap("error", "Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));
        user.setRoles(Collections.singletonList(userRole));

        userRepository.save(user);
        return Collections.singletonMap("message", "User registered successfully");
    }

    @GetMapping("/oauth2/popup-success")
    public String oauth2PopupSuccess(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SecurityContext context = (SecurityContext) request.getSession()
                .getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);

        if (context != null && context.getAuthentication() != null) {
            CustomUserPrincipal principal = (CustomUserPrincipal) context.getAuthentication().getPrincipal();
            User user = principal.getUser();
            String userJson = new ObjectMapper().writeValueAsString(UserMapper.toResponse(user));

            // Return HTML with a script that sends data to the opener
            response.setContentType("text/html");
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
        }
        return null;
    }



}
