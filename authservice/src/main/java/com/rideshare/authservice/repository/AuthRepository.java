package com.rideshare.authservice.repository;

import com.rideshare.authservice.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthRepository extends JpaRepository<AuthUser, UUID> {
    boolean existsByEmail(String email);
    Optional<AuthUser> findByEmail(String email);
}
