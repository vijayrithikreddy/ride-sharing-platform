package com.rideshare.userservice.repository;


import com.rideshare.userservice.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile,Integer> {
    Optional<UserProfile> findByAuthUserId(UUID id);
    boolean existsByAuthUserId(UUID id);
}
