package io.bootify.saven.repos;

import io.bootify.saven.domain.Leaderboard;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LeaderboardRepository extends JpaRepository<Leaderboard, UUID> {
    List<Leaderboard> findByUtilityTypeAndTimeWindow(int utilityType, int timeWindow);
}
