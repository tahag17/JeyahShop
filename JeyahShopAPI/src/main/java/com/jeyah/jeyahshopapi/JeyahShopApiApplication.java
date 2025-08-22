package com.jeyah.jeyahshopapi;

import com.jeyah.jeyahshopapi.role.Role;
import com.jeyah.jeyahshopapi.role.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableWebSecurity
public class JeyahShopApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(JeyahShopApiApplication.class, args);
    }
    @Bean
    public CommandLineRunner addRoles(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.count() == 0) {
                Role user = new Role();
                user.setName("ROLE_USER");
                roleRepository.save(user);

                Role manager = new Role();
                manager.setName("ROLE_MANAGER");
                roleRepository.save(manager);

                Role admin = new Role();
                admin.setName("ROLE_ADMIN");
                roleRepository.save(admin);
            }
        };
    }
}
