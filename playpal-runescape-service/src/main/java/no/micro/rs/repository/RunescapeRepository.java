package no.micro.rs.repository;

import no.micro.rs.model.RunescapeChar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RunescapeRepository extends JpaRepository<RunescapeChar, Long> {
    RunescapeChar findByRunescapeName(String runescapeName);
    RunescapeChar findByUserId(Long userId);
}
