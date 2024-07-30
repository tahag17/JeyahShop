package com.jeyah.jeyahshopapi.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRespository extends JpaRepository<Token, Integer> {
    Optional<Token> findByToken(String token);
}
