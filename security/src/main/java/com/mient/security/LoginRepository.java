package com.mient.security;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginRepository extends JpaRepository<LoginEntity, Integer> {
    Optional<LoginEntity> findByToken(String token);
}
