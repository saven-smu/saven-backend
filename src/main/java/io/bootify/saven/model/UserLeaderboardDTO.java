package io.bootify.saven.model;

import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserLeaderboardDTO {

    private UUID id;

    @NotNull
    private Integer position;

    @NotNull
    private Double computedScore;

    private UUID leaderboard;

    private UUID user;

}
