package io.bootify.saven.model;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class LeaderboardDTO {

    private UUID id;

    @NotNull
    private Integer utilityType;

    @NotNull
    private Integer timeWindow;

    @NotNull
    private LocalDateTime storedDateTime;
}
