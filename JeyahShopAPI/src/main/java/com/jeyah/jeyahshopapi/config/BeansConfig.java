package com.jeyah.jeyahshopapi.config;

import com.jeyah.jeyahshopapi.AuditorAwareImpl;
import com.jeyah.jeyahshopapi.user.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfig {
    private final UserRepository userRepository;

    public BeansConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public AuditorAwareImpl auditorAware() {
        return new AuditorAwareImpl(userRepository);
    }
}
