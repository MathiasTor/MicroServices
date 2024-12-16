package no.micro.livesearch_service2.repo;

import no.micro.livesearch_service2.model.LiveUserSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LiveUserSearchRepository extends JpaRepository<LiveUserSearch, Long> {
    LiveUserSearch findByUserId(Long userId);
    List<LiveUserSearch> findByUserIdAndActive(Long userId, int active);
    List<LiveUserSearch> findByUserIdAndReadMessageAndMatchedUserId(Long userId, int readMessage, long matchedUserId);
    List<LiveUserSearch> findByActive(int active);


    @Query("SELECT l FROM LiveUserSearch l " +
            "WHERE l.userId = :userId " +
            "AND l.readMessage = 0 " +
            "AND l.active = 0 " +
            "AND l.matchedUserId > 0")
    List<LiveUserSearch> findUnreadMatches(@Param("userId") Long userId);

}
