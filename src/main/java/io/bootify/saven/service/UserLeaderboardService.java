package io.bootify.saven.service;

import io.bootify.saven.domain.Leaderboard;
import io.bootify.saven.domain.User;
import io.bootify.saven.domain.UserLeaderboard;
import io.bootify.saven.model.UserLeaderboardDTO;
import io.bootify.saven.repos.LeaderboardRepository;
import io.bootify.saven.repos.UserLeaderboardRepository;
import io.bootify.saven.repos.UserRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class UserLeaderboardService {

    private final UserLeaderboardRepository userLeaderboardRepository;
    private final LeaderboardRepository leaderboardRepository;
    private final UserRepository userRepository;

    public UserLeaderboardService(final UserLeaderboardRepository userLeaderboardRepository,
            final LeaderboardRepository leaderboardRepository,
            final UserRepository userRepository) {
        this.userLeaderboardRepository = userLeaderboardRepository;
        this.leaderboardRepository = leaderboardRepository;
        this.userRepository = userRepository;
    }

    public List<UserLeaderboardDTO> findAll() {
        return userLeaderboardRepository.findAll(Sort.by("id"))
                .stream()
                .map(userLeaderboard -> mapToDTO(userLeaderboard, new UserLeaderboardDTO()))
                .collect(Collectors.toList());
    }

    public UserLeaderboardDTO get(final UUID id) {
        return userLeaderboardRepository.findById(id)
                .map(userLeaderboard -> mapToDTO(userLeaderboard, new UserLeaderboardDTO()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public UUID create(final UserLeaderboardDTO userLeaderboardDTO) {
        final UserLeaderboard userLeaderboard = new UserLeaderboard();
        mapToEntity(userLeaderboardDTO, userLeaderboard);
        return userLeaderboardRepository.save(userLeaderboard).getId();
    }

    public void update(final UUID id, final UserLeaderboardDTO userLeaderboardDTO) {
        final UserLeaderboard userLeaderboard = userLeaderboardRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        mapToEntity(userLeaderboardDTO, userLeaderboard);
        userLeaderboardRepository.save(userLeaderboard);
    }

    public void delete(final UUID id) {
        userLeaderboardRepository.deleteById(id);
    }

    private UserLeaderboardDTO mapToDTO(final UserLeaderboard userLeaderboard,
            final UserLeaderboardDTO userLeaderboardDTO) {
        userLeaderboardDTO.setId(userLeaderboard.getId());
        userLeaderboardDTO.setPosition(userLeaderboard.getPosition());
        userLeaderboardDTO.setComputedScore(userLeaderboard.getComputedScore());
        userLeaderboardDTO.setLeaderboard(userLeaderboard.getLeaderboard() == null ? null : userLeaderboard.getLeaderboard().getId());
        userLeaderboardDTO.setUser(userLeaderboard.getUser() == null ? null : userLeaderboard.getUser().getId());
        return userLeaderboardDTO;
    }

    private UserLeaderboard mapToEntity(final UserLeaderboardDTO userLeaderboardDTO,
            final UserLeaderboard userLeaderboard) {
        userLeaderboard.setPosition(userLeaderboardDTO.getPosition());
        userLeaderboard.setComputedScore(userLeaderboardDTO.getComputedScore());
        final Leaderboard leaderboard = userLeaderboardDTO.getLeaderboard() == null ? null : leaderboardRepository.findById(userLeaderboardDTO.getLeaderboard())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "leaderboard not found"));
        userLeaderboard.setLeaderboard(leaderboard);
        final User user = userLeaderboardDTO.getUser() == null ? null : userRepository.findById(userLeaderboardDTO.getUser())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));
        userLeaderboard.setUser(user);
        return userLeaderboard;
    }

}
