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
//    @Bean
//    public CommandLineRunner addRoles(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
//        return args -> {
//            if (roleRepository.count() == 0) {
//                Role user = new Role();
//                user.setName("ROLE_USER");
//                roleRepository.save(user);
//
//                Role manager = new Role();
//                manager.setName("ROLE_MANAGER");
//                roleRepository.save(manager);
//
//                Role admin = new Role();
//                admin.setName("ROLE_ADMIN");
//                roleRepository.save(admin);
//
//                // Manager user
//                User managerUser = new User();
//                managerUser.setFirstName("Manager");
//                managerUser.setLastName("User");
//                managerUser.setEmail("manager@example.com");
//                managerUser.setPassword(passwordEncoder.encode("manager123"));
//                managerUser.setEnabled(true);
//                managerUser.setRoles(new ArrayList<>());
//                managerUser.getRoles().add(manager);
//                userRepository.save(managerUser);
//
//                // Admin user
//                User adminUser = new User();
//                adminUser.setFirstName("Admin");
//                adminUser.setLastName("User");
//                adminUser.setEmail("admin@example.com");
//                adminUser.setPassword(passwordEncoder.encode("admin123"));
//                adminUser.setEnabled(true);
//                adminUser.setRoles(new ArrayList<>());
//                adminUser.getRoles().add(admin);
//                userRepository.save(adminUser);
//            }
//
//        };
//    }

        @Bean
    public CommandLineRunner addRoles(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            // 1️⃣ Create roles if missing
            Role roleUser = roleRepository.findByName("ROLE_USER").orElseGet(null);
            Role roleManager = roleRepository.findByName("ROLE_MANAGER").orElseGet(null);
            Role roleAdmin = roleRepository.findByName("ROLE_ADMIN").orElseGet(null);

            userRepository.findByEmail("manager@example.com").orElseGet(() -> {
                User manager = new User();
                manager.setFirstName("Manager");
                manager.setLastName("User");
                manager.setEmail("manager@example.com");
                manager.setPassword(passwordEncoder.encode("manager123"));
                manager.setEnabled(true);
                manager.setAccountLocked(false);
                manager.setRoles(new ArrayList<>(List.of(roleManager)));
                return userRepository.save(manager);
            });

            // 3️⃣ Create Admin if missing
            userRepository.findByEmail("admin@example.com").orElseGet(() -> {
                User admin = new User();
                admin.setFirstName("Admin");
                admin.setLastName("User");
                admin.setEmail("admin@example.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setEnabled(true);
                admin.setAccountLocked(false);
                admin.setRoles(new ArrayList<>(List.of(roleAdmin)));
                return userRepository.save(admin);
            });


        };
    }

}
