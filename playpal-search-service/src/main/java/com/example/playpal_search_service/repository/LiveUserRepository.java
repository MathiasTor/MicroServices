package com.example.playpal_search_service.repository;

import com.example.playpal_search_service.model.LiveUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LiveUserRepository extends JpaRepository<LiveUser, Long> {
    //List<LiveUser> findAllByLiveStartTimeAfter(LocalDateTime cutoff);

    // To disable isLive on previous posts when making new.
    List<LiveUser> findByUserIdAndIsLiveTrue(Long userId);

    // Important to differntiate between primary key ID and the actual user ID
    List<LiveUser> findByUserId(Long userId);
}