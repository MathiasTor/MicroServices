package com.example.playpalleaderboardservice.Repository;

import com.example.playpalleaderboardservice.Model.Leaderboard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeaderboardRepo extends JpaRepository<Leaderboard, Long> {

    List<Leaderboard> findAllByOrderByTotalRaidsDesc();


    Optional<Leaderboard> findByUserId(Long userId);

}
