package com.example.playpal_livesearch_service.repository;

import com.example.playpal_livesearch_service.models.LiveUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LiveUserRepository extends JpaRepository<LiveUser, Long> {
    List<LiveUser> findByIsLiveTrue();
    List<LiveUser> findByUserId(Long userId);
    List<LiveUser> findByUserIdAndIsLiveTrue(Long userId); // Spring Data JPA will create the query automatically
    List<LiveUser> findByLiveStartTimeBeforeAndIsLiveTrue(LocalDateTime liveStartTime);
}
