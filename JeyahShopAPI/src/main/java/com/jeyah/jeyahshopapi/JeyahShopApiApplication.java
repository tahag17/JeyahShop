package com.jeyah.jeyahshopapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JeyahShopApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(JeyahShopApiApplication.class, args);
    }

}
