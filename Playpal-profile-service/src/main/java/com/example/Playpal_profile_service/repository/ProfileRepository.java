package com.example.Playpal_profile_service.repository;

import com.example.Playpal_profile_service.model.PlaypalProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<PlaypalProfile, Long> {
    Optional<PlaypalProfile> findByUserId(Long userId);
}
