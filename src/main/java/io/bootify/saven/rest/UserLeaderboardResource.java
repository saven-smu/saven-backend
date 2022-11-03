package io.bootify.saven.rest;

import io.bootify.saven.model.UserLeaderboardDTO;
import io.bootify.saven.service.UserLeaderboardService;
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
@RequestMapping(value = "/api/userLeaderboards", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserLeaderboardResource {

    private final UserLeaderboardService userLeaderboardService;

    public UserLeaderboardResource(final UserLeaderboardService userLeaderboardService) {
        this.userLeaderboardService = userLeaderboardService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('read:userLeaderboards')")
    public ResponseEntity<List<UserLeaderboardDTO>> getAllUserLeaderboards() {
        return ResponseEntity.ok(userLeaderboardService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserLeaderboardDTO> getUserLeaderboard(@PathVariable final UUID id) {
        return ResponseEntity.ok(userLeaderboardService.get(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('upload:userLeaderboard')")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<UUID> createUserLeaderboard(
            @RequestBody @Valid final UserLeaderboardDTO userLeaderboardDTO) {
        return new ResponseEntity<>(userLeaderboardService.create(userLeaderboardDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('update:userLeaderboard')")
    public ResponseEntity<Void> updateUserLeaderboard(@PathVariable final UUID id,
            @RequestBody @Valid final UserLeaderboardDTO userLeaderboardDTO) {
        userLeaderboardService.update(id, userLeaderboardDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('delete:userLeaderboard')")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteUserLeaderboard(@PathVariable final UUID id) {
        userLeaderboardService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
