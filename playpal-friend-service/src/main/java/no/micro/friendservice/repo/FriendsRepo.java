package no.micro.friendservice.repo;

import no.micro.friendservice.model.Friends;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendsRepo extends JpaRepository<Friends, Long> {
    Friends findByUserId(Long userId);
}
