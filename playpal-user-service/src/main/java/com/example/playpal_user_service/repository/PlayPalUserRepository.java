package com.example.playpal_user_service.repository;

import com.example.playpal_user_service.model.PlayPalUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayPalUserRepository extends JpaRepository<PlayPalUser, Long> {

    Optional<PlayPalUser> findByUsername(String username);
    Optional<PlayPalUser> findByEmail(String email);

}
