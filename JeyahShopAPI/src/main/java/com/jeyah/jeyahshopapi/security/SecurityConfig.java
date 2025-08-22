package com.jeyah.jeyahshopapi.security;

import com.jeyah.jeyahshopapi.user.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    //        private final JwtAuthConverter jwtAuthConverter;
    private final CustomUserDetailsService userDetailsService; // ✅ inject here

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomOidcUserService customOidcUserService) throws Exception {
        http
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("user/**").hasRole("USER")
                        .requestMatchers("manager/**").hasRole("MANAGER")
                        .requestMatchers("admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
//                .formLogin(Customizer.withDefaults())
                .logout(Customizer.withDefaults())
//                    .oauth2ResourceServer(oauth2 -> oauth2
//                            .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter))
//                    )
//                    .sessionManagement(session -> session
//                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                    )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.oidcUserService(customOidcUserService)))
                .sessionManagement(session -> session.maximumSessions(1));

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}