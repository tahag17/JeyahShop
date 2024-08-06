package com.jeyah.jeyahshopapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;

public class BeansConfig {
    @Bean
    public AuditorAware<String> auditorAware(){return new ApplicationAuditAware();}
}
