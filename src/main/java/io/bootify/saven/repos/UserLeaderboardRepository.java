package io.bootify.saven.repos;

import io.bootify.saven.domain.UserLeaderboard;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserLeaderboardRepository extends JpaRepository<UserLeaderboard, UUID> {
}
