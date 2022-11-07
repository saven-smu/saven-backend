package io.bootify.saven.service;

import io.bootify.saven.domain.Leaderboard;
import io.bootify.saven.model.LeaderboardDTO;
import io.bootify.saven.repos.LeaderboardRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class LeaderboardService {

    private final LeaderboardRepository leaderboardRepository;

    public LeaderboardService(final LeaderboardRepository leaderboardRepository) {
        this.leaderboardRepository = leaderboardRepository;
    }

    public List<LeaderboardDTO> findAll() {
        return leaderboardRepository.findAll(Sort.by("id"))
                .stream()
                .map(leaderboard -> mapToDTO(leaderboard, new LeaderboardDTO()))
                .collect(Collectors.toList());
    }

    public LeaderboardDTO get(final UUID id) {
        return leaderboardRepository.findById(id)
                .map(leaderboard -> mapToDTO(leaderboard, new LeaderboardDTO()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public List<LeaderboardDTO> getPastLeaderboard(final int utilityType, final int numDays) {
        return leaderboardRepository.findByUtilityTypeAndStoredDateTimeAfter(utilityType, LocalDateTime.now().minusDays(numDays))
                .stream()
                .map(leaderboard -> mapToDTO(leaderboard, new LeaderboardDTO()))
                .collect(Collectors.toList());
    }

    public List<LeaderboardDTO> getLeaderboardsTimeWindowAndUtilityType(final int utilityType, final int time_window) {
        return leaderboardRepository.findByUtilityTypeAndTimeWindow(utilityType, time_window)
                .stream()
                .map(leaderboard -> mapToDTO(leaderboard, new LeaderboardDTO()))
                .collect(Collectors.toList());
    }

    public List<LeaderboardDTO> getLeaderboardsTimeWindow(final int time_window) {
        return leaderboardRepository.findByTimeWindow(time_window)
                .stream()
                .map(leaderboard -> mapToDTO(leaderboard, new LeaderboardDTO()))
                .collect(Collectors.toList());
    }

    public UUID create(final LeaderboardDTO leaderboardDTO) {
        final Leaderboard leaderboard = new Leaderboard();
        mapToEntity(leaderboardDTO, leaderboard);
        return leaderboardRepository.save(leaderboard).getId();
    }

    public void update(final UUID id, final LeaderboardDTO leaderboardDTO) {
        final Leaderboard leaderboard = leaderboardRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        mapToEntity(leaderboardDTO, leaderboard);
        leaderboardRepository.save(leaderboard);
    }

    public void delete(final UUID id) {
        leaderboardRepository.deleteById(id);
    }

    private LeaderboardDTO mapToDTO(final Leaderboard leaderboard,
            final LeaderboardDTO leaderboardDTO) {
        leaderboardDTO.setId(leaderboard.getId());
        leaderboardDTO.setUtilityType(leaderboard.getUtilityType());
        leaderboardDTO.setTimeWindow(leaderboard.getTimeWindow());
        leaderboardDTO.setStoredDateTime(leaderboard.getStoredDateTime());
        return leaderboardDTO;
    }

    private Leaderboard mapToEntity(final LeaderboardDTO leaderboardDTO,
            final Leaderboard leaderboard) {
        leaderboard.setUtilityType(leaderboardDTO.getUtilityType());
        leaderboard.setTimeWindow(leaderboardDTO.getTimeWindow());
        leaderboard.setStoredDateTime(leaderboardDTO.getStoredDateTime());
        return leaderboard;
    }

}
