package no.micro.livesearch_service2.repo;

import no.micro.livesearch_service2.model.LiveUserSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LiveUserSearchRepository extends JpaRepository<LiveUserSearch, Long> {
    LiveUserSearch findByUserId(Long userId);
}
