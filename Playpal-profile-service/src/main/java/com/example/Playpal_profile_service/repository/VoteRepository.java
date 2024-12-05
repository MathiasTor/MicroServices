package com.example.Playpal_profile_service.repository;

import com.example.Playpal_profile_service.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByProfileIdAndVoterId(Long profileId, Long voterId);
}
