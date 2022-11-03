package io.bootify.saven.rest;

import io.bootify.saven.model.LeaderboardDTO;
import io.bootify.saven.service.LeaderboardService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/leaderboards", produces = MediaType.APPLICATION_JSON_VALUE)
public class LeaderboardResource {

    private final LeaderboardService leaderboardService;

    public LeaderboardResource(final LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping
    public ResponseEntity<List<LeaderboardDTO>> getAllLeaderboards() {
        return ResponseEntity.ok(leaderboardService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('read:leaderboard')")
    public ResponseEntity<LeaderboardDTO> getLeaderboard(@PathVariable final UUID id) {
        return ResponseEntity.ok(leaderboardService.get(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('upload:leaderboard')")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<UUID> createLeaderboard(
            @RequestBody @Valid final LeaderboardDTO leaderboardDTO) {
        return new ResponseEntity<>(leaderboardService.create(leaderboardDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('update:leaderboard')")
    public ResponseEntity<Void> updateLeaderboard(@PathVariable final UUID id,
            @RequestBody @Valid final LeaderboardDTO leaderboardDTO) {
        leaderboardService.update(id, leaderboardDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('delete:leaderboard')")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteLeaderboard(@PathVariable final UUID id) {
        leaderboardService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
