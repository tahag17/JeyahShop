package com.jeyah.jeyahshopapi;

import com.jeyah.jeyahshopapi.role.Role;
import com.jeyah.jeyahshopapi.role.RoleRepository;
import com.jeyah.jeyahshopapi.user.User;
import com.jeyah.jeyahshopapi.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableWebSecurity
public class JeyahShopApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(JeyahShopApiApplication.class, args);
    }

    @Bean
    public CommandLineRunner addRoles(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            // Fetch ROLE_USER from the DB
            Role roleUser = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("ROLE_USER not found in DB"));

            // Create 20 normal users
            for (int i = 1; i <= 20; i++) {
                final String email = "user" + i + "@example.com";

                // Check if the user already exists
                int finalI = i;
                userRepository.findByEmail(email).orElseGet(() -> {
                    User user = new User();
                    user.setFirstName("User" + finalI);
                    user.setLastName("Test");
                    user.setEmail(email);
                    user.setPassword(passwordEncoder.encode("password" + finalI)); // default password
                    user.setEnabled(true);
                    user.setAccountLocked(false);
                    user.setRoles(new ArrayList<>(List.of(roleUser)));

                    return userRepository.save(user);
                });
            }
        };
    }
}
