package io.bootify.saven.model;

import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class LeaderboardDTO {

    private UUID id;

    @NotNull
    private Integer month;

}
